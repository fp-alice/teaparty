package wtf.shekels.alice.teaparty.common.messages.outgoing

import scodec._
import scodec.codecs._
import wtf.shekels.alice.teaparty.common.messages.Outgoing
import wtf.shekels.alice.teaparty.common.model.Position

case class ServerUpdateEntityPosition(id: Byte, position: Position) extends Outgoing {
  override def forUser(user: String): Boolean = true
}

object ServerUpdateEntityPosition {
  val codec: Codec[ServerUpdateEntityPosition] =
    (("id" | byte) :: ("position" | Position.codec)).as[ServerUpdateEntityPosition]
}
