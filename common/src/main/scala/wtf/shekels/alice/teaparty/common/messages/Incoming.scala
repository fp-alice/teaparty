package wtf.shekels.alice.teaparty.common.messages

import scodec._
import scodec.codecs._
import wtf.shekels.alice.teaparty.common.messages.incoming._

trait Incoming

object Incoming {
  val codec: Codec[Incoming] =
    discriminated[Incoming]
      .by(uint8)
      .typecase(0, ClientConnected.codec)
      .typecase(1, ClientDisconnected.codec)
      .typecase(2, ClientUpdatePosition.codec)
      .typecase(3, ClientUpdateServer.codec)
      .typecase(4, ClientMessage.codec)
      .typecase(5, ClientUpdateDimension.codec)
      .typecase(6, ClientRegisterEntity.codec)
      .typecase(7, ClientUpdateEntityPosition.codec)
}
