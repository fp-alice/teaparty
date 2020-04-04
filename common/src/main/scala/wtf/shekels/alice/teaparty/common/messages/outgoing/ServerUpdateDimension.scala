package wtf.shekels.alice.teaparty.common.messages.outgoing

import scodec._
import scodec.codecs._
import wtf.shekels.alice.teaparty.common.messages.Outgoing
import wtf.shekels.alice.teaparty.common.model.Dimension

case class ServerUpdateDimension(account: String, dimension: Dimension) extends Outgoing {
  override def forUser(user: String): Boolean = user != account
}

object ServerUpdateDimension {
  val codec: Codec[ServerUpdateDimension] =
    (("account" | ascii32) :: ("dimension" | Dimension.codec)).as[ServerUpdateDimension]
}
