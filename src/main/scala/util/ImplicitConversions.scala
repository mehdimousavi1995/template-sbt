package util

trait ImplicitConversions {
  implicit def toOwner(owner: persist.cassandra.owner.Owner): messages.homeee.homessages.Owner =
    messages.homeee.homessages.Owner(owner.ownerId.toString, owner.firstName, owner.lastName, owner.telegramUserId)

}
