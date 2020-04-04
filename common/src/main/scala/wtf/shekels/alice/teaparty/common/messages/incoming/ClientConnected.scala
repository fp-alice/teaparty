package wtf.shekels.alice.teaparty.common.messages.incoming

import scodec._
import scodec.codecs.provide
import wtf.shekels.alice.teaparty.common.messages.Incoming

case object ClientConnected extends Incoming {
  val codec: Codec[ClientConnected.type] = provide(ClientConnected)
}
