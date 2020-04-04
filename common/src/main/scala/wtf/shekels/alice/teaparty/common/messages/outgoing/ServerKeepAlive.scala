package wtf.shekels.alice.teaparty.common.messages.outgoing

import scodec._
import scodec.codecs.provide
import wtf.shekels.alice.teaparty.common.messages.Outgoing

case object ServerKeepAlive extends Outgoing {
  val codec: Codec[ServerKeepAlive.type]      = provide(ServerKeepAlive)
  override def forUser(user: String): Boolean = true
}
