package wtf.shekels.alice.teaparty.server.model

import monocle.std.option._
import monocle.{PLens, POptional}
import UserInfo._
import UserConnection._
import wtf.shekels.alice.teaparty.common.model.Position
import ServerDetails._

object Lenses {
  val serverDetailsLens: PLens[UserConnection, UserConnection, Option[ServerDetails], Option[ServerDetails]] =
    _userInfo composeLens _serverDetails

  val currentServerLens: POptional[UserConnection, UserConnection, String, String] =
    serverDetailsLens composePrism some composeLens _currentServer

  val currentPositionLens: POptional[UserConnection, UserConnection, Position, Position] =
    serverDetailsLens composePrism some composeLens _position
}
