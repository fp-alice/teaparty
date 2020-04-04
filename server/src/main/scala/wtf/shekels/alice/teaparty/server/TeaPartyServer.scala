package wtf.shekels.alice.teaparty.server

import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import fs2.Stream
import fs2.concurrent.{Queue, Topic}
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import wtf.shekels.alice.teaparty.common.messages.Outgoing
import wtf.shekels.alice.teaparty.common.messages.outgoing.ServerKeepAlive
import wtf.shekels.alice.teaparty.server.model.{TeaPartyState, UserContext}

import scala.concurrent.duration._
import scala.util.Try

object TeaPartyServer extends IOApp {

  def createProcessingStream(
      port: Int,
      queue: Queue[IO, UserContext],
      topic: Topic[IO, Outgoing],
      ref: Ref[IO, TeaPartyState]
  ): IO[ExitCode] = {
    // Stream for HTTP requests
    val httpStream = ServerStream.stream[IO](port, ref, queue, topic)

    // Stream to keep alive idle WebSockets
    val keepAlive =
      Stream.awakeEvery[IO](30.seconds).map(_ => ServerKeepAlive).through(topic.publish)

    // Stream to process items from the queue and publish the results to the topic
    // 1. Dequeue
    // 2. apply message to state reference
    // 3. Convert resulting output messages to a stream
    // 4. Publish output messages to the publish/subscribe topic
    val processingStream =
      queue.dequeue
        .evalMap(msg => ref.modify(_.process(msg)))
        .flatMap(Stream.emits)
        .through(topic.publish)

    // fs2 Streams must be "pulled" to process messages. Drain will perpetually pull our top-level streams
    Stream(httpStream, keepAlive, processingStream).parJoinUnbounded.compile.drain
      .as(ExitCode.Success)
  }

  def run(args: List[String]): IO[ExitCode] = {
    val httpPort = args.headOption
      .orElse(sys.env.get("PORT"))
      .flatMap(s => Try(s.toInt).toOption) // Ignore any integer parse errors
      .getOrElse(8080)

    for {
      // Synchronization objects must be created at a level where they can be shared with every object that needs them
      queue <- Queue.unbounded[IO, UserContext]
      topic <- Topic[IO, Outgoing](ServerKeepAlive)

      // There are a few ways to represent state in this model. We choose functional references so that the
      // state can be referenced in multiple locations. If the state is only needed in a single location
      // then there are simpler models (like Stream.scan and Stream.mapAccumulate)
      ref <- Ref.of[IO, TeaPartyState](TeaPartyState())

      // Create and then combine the top-level streams for our application
      exitCode <- createProcessingStream(httpPort, queue, topic, ref)
    } yield exitCode
  }
}

object ServerStream {
  // Builds a stream for HTTP events processed by our router
  def stream[F[_]: ConcurrentEffect: Timer: ContextShift](
      port: Int,
      teaPartyState: Ref[F, TeaPartyState],
      queue: Queue[F, UserContext],
      topic: Topic[F, Outgoing]
  ): fs2.Stream[F, ExitCode] =
    BlazeServerBuilder[F]
      .bindHttp(port, "0.0.0.0")
      .withHttpApp(
        Router(
          "/" -> new TeaPartyRoutes[F](teaPartyState, queue, topic).routes
        ).orNotFound
      )
      .serve
}
