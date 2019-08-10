package botaccess

import akka.persistence.SnapshotMetadata
import cqrs.{Event, ProcessorState}
import messages.botaccess.BotAccessEvents._
import messages.botaccess.BotAccessSnapShot
private[botaccess] object BotAccessState {

  def initial(botUserId: Int) = BotAccessState(
    id = botUserId,
    roles = Seq("default-role"),
    premiumAccesses = Seq.empty
  )
}

private[botaccess] final case class BotAccessState(
  id:              Int,
  roles:           Seq[String],
  premiumAccesses: Seq[String]
) extends ProcessorState[BotAccessState] {

  override def updated(e: Event): BotAccessState = {
    e match {
      case AccessGranted(_, rs) ⇒
        this.copy(roles = (this.roles ++ rs).distinct)
      case AccessRevoked(_, rs) ⇒
        this.copy(roles = this.roles.filterNot(p ⇒ rs.contains(p)))
      case PremiumAccessGranted(_, pa) ⇒
        this.copy(premiumAccesses = (this.premiumAccesses ++ pa).distinct)
      case PremiumAccessRevoked(_, pa) ⇒
        this.copy(premiumAccesses = this.premiumAccesses.filterNot(p ⇒ pa.contains(p)))
    }
  }

  override def withSnapshot(metadata: SnapshotMetadata, snapshot: Any): BotAccessState = snapshot match {
    case s: BotAccessSnapShot ⇒
      copy(
        id = s.id,
        roles = s.roles,
        premiumAccesses = s.premiumAccesses
      )
  }

  override lazy val snapshot = BotAccessSnapShot(
    id = id,
    roles = roles
  )
}

