package wtf.shekels.alice.teaparty.common.model

import java.util.UUID

import monocle.macros.Lenses
import scodec._
import scodec.codecs._

@Lenses("entity")
case class RegisteredEntity(id: Byte, uuid: UUID, position: Position = Position())

object RegisteredEntity {
  val codec: Codec[RegisteredEntity] =
    (("id" | byte) :: ("uuid" | uuid) :: ("position" | Position.codec)).as[RegisteredEntity]
}
