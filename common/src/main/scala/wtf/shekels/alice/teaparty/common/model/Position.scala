package wtf.shekels.alice.teaparty.common.model

import monocle.macros.Lenses
import scodec._
import scodec.codecs._

@Lenses("position")
case class Position(x: Double = 0, y: Double = 0, z: Double = 0)

object Position {
  val codec: Codec[Position] = (("x" | double) :: ("y" | double) :: ("z" | double)).as[Position]
}
