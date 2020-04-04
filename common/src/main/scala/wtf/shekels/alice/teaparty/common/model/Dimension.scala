package wtf.shekels.alice.teaparty.common.model

import scodec.Codec
import scodec.codecs.{byte, discriminated, provide}

sealed trait Dimension

object Dimension {
  case object Overworld extends Dimension
  case object Nether    extends Dimension
  case object End       extends Dimension
  val codec: Codec[Dimension] =
    discriminated[Dimension]
      .by(byte)
      .typecase(0, provide(Overworld))
      .typecase(1, provide(Nether))
      .typecase(2, provide(End))
}
