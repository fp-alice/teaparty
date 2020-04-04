package wtf.shekels.alice.teaparty.common.messages.incoming

import scodec._
import scodec.codecs._
import wtf.shekels.alice.teaparty.common.messages.Incoming
import wtf.shekels.alice.teaparty.common.model.Position

case class ClientUpdateEntityPosition(id: Byte, position: Position) extends Incoming

object ClientUpdateEntityPosition {
  val codec: Codec[ClientUpdateEntityPosition] =
    (("id" | byte) :: ("position" | Position.codec)).as[ClientUpdateEntityPosition]
}
