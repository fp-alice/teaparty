package wtf.shekels.alice.teaparty.common.messages.incoming

import scodec.Codec
import scodec.codecs._
import wtf.shekels.alice.teaparty.common.messages.Incoming
import wtf.shekels.alice.teaparty.common.model.Position

case class ClientUpdatePosition(position: Position) extends Incoming

case object ClientUpdatePosition {
  val codec: Codec[ClientUpdatePosition] = ("position" | Position.codec).as[ClientUpdatePosition]
}
