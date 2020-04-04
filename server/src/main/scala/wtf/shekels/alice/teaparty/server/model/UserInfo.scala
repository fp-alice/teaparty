package wtf.shekels.alice.teaparty.server.model

import monocle.macros.Lenses

@Lenses("_")
case class UserInfo(currentAltUsername: String, serverDetails: Option[ServerDetails] = None)
