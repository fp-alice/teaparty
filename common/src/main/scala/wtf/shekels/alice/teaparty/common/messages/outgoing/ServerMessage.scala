package wtf.shekels.alice.teaparty.common.messages.outgoing

import scodec._
import scodec.codecs._
import wtf.shekels.alice.teaparty.common.messages.Outgoing

case class ServerMessage(from: String, message: String) extends Outgoing {
  override def forUser(user: String): Boolean = from != user
}

object ServerMessage {
  val codec: Codec[ServerMessage] = (("from" | ascii32) :: ("message" | ascii32)).as[ServerMessage]
}
