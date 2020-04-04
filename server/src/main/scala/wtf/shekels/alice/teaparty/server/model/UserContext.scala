package wtf.shekels.alice.teaparty.server.model

import wtf.shekels.alice.teaparty.common.messages.Incoming

case class UserContext(account: String, packet: Incoming)
