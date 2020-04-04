package wtf.shekels.alice.teaparty.common.messages.outgoing

import scodec._
import scodec.codecs._
import wtf.shekels.alice.teaparty.common.messages.Outgoing

case class ServerAnnounceDisconnect(user: String) extends Outgoing {
  override def forUser(user: String): Boolean = true
}

object ServerAnnounceDisconnect {
  val codec: Codec[ServerAnnounceDisconnect] = ("user" | ascii).as[ServerAnnounceDisconnect]
}
