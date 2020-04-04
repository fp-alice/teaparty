package wtf.shekels.alice.teaparty.server.model

import monocle.macros.Lenses

@Lenses("_")
case class UserConnection(
    account: String,
    userInfo: UserInfo
) {
  override def toString: String = s"$account - ${userInfo.toString}"
}
