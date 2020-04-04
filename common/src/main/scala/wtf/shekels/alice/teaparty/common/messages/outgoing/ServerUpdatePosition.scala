package wtf.shekels.alice.teaparty.common.messages.outgoing

import scodec._
import scodec.codecs._
import wtf.shekels.alice.teaparty.common.messages.Outgoing
import wtf.shekels.alice.teaparty.common.model.Position

case class ServerUpdatePosition(account: String, position: Position) extends Outgoing {
  override def forUser(user: String): Boolean = user != account
}

object ServerUpdatePosition {
  val codec: Codec[ServerUpdatePosition] =
    (("account" | ascii32) :: ("position" | Position.codec)).as[ServerUpdatePosition]
}
