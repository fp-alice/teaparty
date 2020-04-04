package wtf.shekels.alice.teaparty.server.model

import monocle.macros.Lenses
import wtf.shekels.alice.teaparty.common.model.{Dimension, Position}

@Lenses("_")
case class ServerDetails(currentServer: String,
                         position: Position = Position(),
                         dimension: Dimension = Dimension.Overworld)
