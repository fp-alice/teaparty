package wtf.shekels.alice.teaparty.common.messages.incoming

import scodec._
import scodec.codecs._
import wtf.shekels.alice.teaparty.common.messages.Incoming
import wtf.shekels.alice.teaparty.common.model.Dimension

case class ClientUpdateDimension(dimension: Dimension) extends Incoming

object ClientUpdateDimension {
  val codec: Codec[ClientUpdateDimension] = ("dimension" | Dimension.codec).as[ClientUpdateDimension]
}
