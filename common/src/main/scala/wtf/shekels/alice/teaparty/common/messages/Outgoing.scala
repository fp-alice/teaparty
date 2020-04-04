package wtf.shekels.alice.teaparty.common.messages

import scodec._
import scodec.codecs._
import wtf.shekels.alice.teaparty.common.messages.outgoing._

trait Outgoing {
  def forUser(user: String): Boolean
}

object Outgoing {
  val codec: Codec[Outgoing] =
    discriminated[Outgoing]
      .by(uint8)
      .typecase(0, ServerKeepAlive.codec)
      .typecase(1, ServerMessage.codec)
      .typecase(2, ServerAnnounceDisconnect.codec)
      .typecase(3, ServerRegisterEntity.codec)
      .typecase(4, ServerUpdateEntityPosition.codec)
      .typecase(5, ServerUpdateDimension.codec)
      .typecase(6, ServerUpdatePosition.codec)
}
