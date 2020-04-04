package wtf.shekels.alice.teaparty.common.messages.incoming

import scodec._
import scodec.codecs._
import wtf.shekels.alice.teaparty.common.messages.Incoming

case class ClientMessage(message: String) extends Incoming

object ClientMessage {
  val codec: Codec[ClientMessage] = ("message" | ascii).as[ClientMessage]
}
