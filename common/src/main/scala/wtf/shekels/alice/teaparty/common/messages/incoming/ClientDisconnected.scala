package wtf.shekels.alice.teaparty.common.messages.incoming

import scodec._
import scodec.codecs.provide
import wtf.shekels.alice.teaparty.common.messages.Incoming

case object ClientDisconnected extends Incoming {
  val codec: Codec[ClientDisconnected.type] = provide(ClientDisconnected)
}
