package wtf.shekels.alice.teaparty.common.messages.incoming

import java.util.UUID

import scodec._
import scodec.codecs._
import wtf.shekels.alice.teaparty.common.messages.Incoming

case class ClientRegisterEntity(uuid: UUID) extends Incoming

object ClientRegisterEntity {
  val codec: Codec[ClientRegisterEntity] = ("uuid" | uuid).as[ClientRegisterEntity]
}
