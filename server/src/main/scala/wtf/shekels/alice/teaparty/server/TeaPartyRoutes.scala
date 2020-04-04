package wtf.shekels.alice.teaparty.server

import cats.effect.concurrent.Ref
import cats.effect.{ContextShift, Sync}
import fs2.concurrent.{Queue, Topic}
import fs2.{Pipe, Stream}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame.{Binary, Close}
import org.http4s.{HttpRoutes, MediaType, Response}
import scodec._
import wtf.shekels.alice.teaparty.common.messages.incoming.{ClientConnected, ClientDisconnected}
import wtf.shekels.alice.teaparty.common.messages.{Incoming, Outgoing}
import wtf.shekels.alice.teaparty.server.model.{TeaPartyState, UserConnection, UserContext}

class TeaPartyRoutes[F[_]: Sync: ContextShift](
    teaPartyState: Ref[F, TeaPartyState],
    queue: Queue[F, UserContext],
    topic: Topic[F, Outgoing]
) extends Http4sDsl[F] {

  val routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "metrics"      => serveMetrics
      case GET -> Root / "ws" / account => handleWebSocketConnection(account)
    }

  private def handleWebSocketConnection(account: String): F[Response[F]] = {

    val toClient: Stream[F, WebSocketFrame.Binary] =
      topic
        .subscribe(1000)
        .filter(_.forUser(account))
        .map(x => WebSocketFrame.Binary(Outgoing.codec.encode(x).require.toByteVector))

    val inputPipe: Pipe[F, WebSocketFrame, Unit] = processInputStream(account)

    WebSocketBuilder[F].build(toClient, inputPipe)
  }

  private def processInputStream(account: String)(wsfStream: Stream[F, WebSocketFrame]): Stream[F, Unit] = {
    val connectPacket                       = UserContext(account, ClientConnected)
    val entryStream: Stream[F, UserContext] = Stream.emits(Seq(connectPacket))
    val parsedInput: Stream[F, UserContext] =
      wsfStream
        .collect {
          case Close(_) =>
            ClientDisconnected
          case Binary(data, _) =>
            Incoming.codec.decode(data.bits) match {
              case Attempt.Successful(value) => value.value
              case _                         => ClientDisconnected
            }
        }
        .map(in => UserContext(account, in))

    (entryStream ++ parsedInput).through(queue.enqueue)
  }

  private def serveMetrics: F[Response[F]] = {
    val outputStream: Stream[F, String] = Stream
      .eval(teaPartyState.get)
      .map(state => generateMetricsHtml(state.connections))
    Ok(outputStream, `Content-Type`(MediaType.text.html))
  }

  private def generateMetricsHtml(users: Set[UserConnection]): String = {
    def list(strings: Set[String]): String = strings.mkString("<ul>\n\t", "\n\t", "\n\t\t\t</ul>")
    def element(u: UserConnection): String = s"\t\t\t<li>$u</li>"
    val elements                           = list(users.map(element))
    s"""|<html>
        |    <title>Tea Party</title>
        |    <body>
        |        <div>
        |            $elements
        |        </div>
        |    </body>
        |</html>
        |""".stripMargin
  }
}
