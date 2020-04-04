package wtf.shekels.alice.teaparty.common.messages.outgoing

import scodec._
import scodec.codecs._
import wtf.shekels.alice.teaparty.common.messages.Outgoing
import wtf.shekels.alice.teaparty.common.model.RegisteredEntity

case class ServerRegisterEntity(entity: RegisteredEntity) extends Outgoing {
  override def forUser(user: String): Boolean = true
}

object ServerRegisterEntity {
  val codec: Codec[ServerRegisterEntity] = ("entity" | RegisteredEntity.codec).as[ServerRegisterEntity]
}
