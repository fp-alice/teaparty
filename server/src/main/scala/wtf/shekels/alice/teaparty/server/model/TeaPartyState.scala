package wtf.shekels.alice.teaparty.server.model

import java.util.UUID

import monocle.Lens
import monocle.macros.{GenLens, Lenses}
import wtf.shekels.alice.teaparty.common.messages.Outgoing
import wtf.shekels.alice.teaparty.common.messages.outgoing._
import wtf.shekels.alice.teaparty.common.messages.incoming._
import wtf.shekels.alice.teaparty.common.model.{Dimension, Position, RegisteredEntity}
import Lenses._

import scala.util.Try

@Lenses("_")
case class TeaPartyState(
    connections: Set[UserConnection] = Set.empty,
    entities: Set[RegisteredEntity] = Set.empty
) {

  type Result = (TeaPartyState, Seq[Outgoing])

  def updateFirst[A](set: Set[A])(predicate: A => Boolean, f: A => A): Set[A] = {
    val seq   = set.toSeq
    val index = seq.indexWhere(predicate)
    seq.updated(index, f(seq(index))).toSet
  }

  def process(userIn: UserContext): Result = userIn.packet match {
    case ClientConnected                     => addConnection(userIn.account)
    case ClientDisconnected                  => removeConnection(userIn.account)
    case ClientUpdatePosition(position)      => updateLocation(userIn.account, position)
    case ClientUpdateServer(server)          => updateServer(userIn.account, server)
    case ClientMessage(message)              => forwardMessage(userIn.account, message)
    case ClientUpdateDimension(dimension)    => clientUpdateDimension(userIn.account, dimension)
    case ClientRegisterEntity(uuid)          => registerEntity(uuid)
    case ClientUpdateEntityPosition(id, pos) => updateEntityPosition(id, pos)
  }

  private def clientUpdateDimension(account: String, dimension: Dimension): Result = {
    val updateDimension = serverDetailsLens.modify {
      case Some(details) => Some(details.copy(dimension = dimension))
      case None          => None
    }
    val next = this.copy(connections = updateFirst(connections)(_.account == account, updateDimension))
    (next, Seq(ServerUpdateDimension(account, dimension)))
  }

  private def updateEntityPosition(id: Byte, pos: Position): Result = {
    val next     = this.copy(entities = updateFirst(entities)(_.id == id, _.copy(position = pos)))
    val response = Seq(ServerUpdateEntityPosition(id, pos))
    (next, response)
  }

  private def registerEntity(uuid: UUID): Result = {
    val id        = Try(entities.map(_.id.toInt).max).getOrElse(-1) + 1
    val newEntity = RegisteredEntity(id.toByte, uuid)
    val next      = this.copy(entities = this.entities + newEntity)
    val response  = Seq(ServerRegisterEntity(newEntity))
    (next, response)
  }

  private def addConnection(account: String): Result = {
    val connection = UserConnection(account, UserInfo("todo: alt username"))
    val next       = TeaPartyState(connections.filter(_.account != account) + connection)
    val response   = Seq(ServerMessage(account, "Connected!"))
    (next, response)
  }

  private def removeConnection(account: String): Result = {
    val next = TeaPartyState(connections.filter(_.account != account))
    val response = Seq(
      ServerAnnounceDisconnect(account),
      ServerMessage(account, s"$account has left the TeaParty")
    )
    (next, response)
  }

  private def updateLocation(account: String, pos: Position): Result = {
    val next     = TeaPartyState(updateFirst(connections)(_.account == account, currentPositionLens.set(pos)))
    val response = Seq(ServerUpdatePosition(account, pos))
    (next, response)
  }

  private def updateServer(account: String, server: Option[String]): Result = server match {
    case Some(server) => setServer(account, server)
    case None         => clearServer(account)
  }

  private def setServer(account: String, server: String): Result = {
    val updateServer = serverDetailsLens.modify {
      case Some(details) => Some(details.copy(currentServer = server))
      case None          => Some(ServerDetails(server))
    }
    val next     = TeaPartyState(updateFirst(connections)(_.account == account, updateServer))
    val response = Seq(ServerMessage(account, s"$account changed servers to $server"))
    (next, response)
  }

  private def clearServer(account: String): Result = {
    val next     = TeaPartyState(updateFirst(connections)(_.account == account, serverDetailsLens.set(None)))
    val response = Seq(ServerMessage(account, s"$account disconnected from the server they were on"))
    (next, response)
  }

  private def forwardMessage(account: String, message: String): Result = {
    val response = Seq(ServerMessage(account, message))
    (this, response)
  }
}
