package wtf.shekels.alice.teaparty.common.messages.incoming

import scodec._
import scodec.codecs._
import wtf.shekels.alice.teaparty.common.messages.Incoming

case class ClientUpdateServer(server: Option[String]) extends Incoming

object ClientUpdateServer {
  val codec: Codec[ClientUpdateServer] = ("server" | optional(bool, ascii)).as[ClientUpdateServer]
}
