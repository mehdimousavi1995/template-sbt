package botaccess

import java.time.Instant

import messages.botaccess.BotAccessEvents._
import messages.botaccess._

private trait BotAccessCommandHandler {
  this: BotAccessProcessor ⇒

  def grantAccess(roles: Seq[String]): Unit = {
    val replyTo = sender
    persist(AccessGranted(Instant.now, roles)) { evt ⇒
      commit(evt)
      log.info("access of roles: {} was granted to bot with id: {}", roles, botUserId)
      replyTo ! ResponseVoid
    }

  }

  def revokeAccess(roles: Seq[String]): Unit = {
    val replyTo = sender
    persist(AccessRevoked(Instant.now, roles)) { evt ⇒
      commit(evt)
      log.info("access of role: {} was taken from bot with id: {}", roles, botUserId)
      replyTo ! ResponseVoid
    }

  }

  def grantPremiumAccess(accesses: Seq[String]): Unit = {
    val replyTo = sender
    persist(PremiumAccessGranted(Instant.now, accesses)) { evt ⇒
      commit(evt)
      log.info("premium access of: {} granted to bot: {}", accesses, botUserId)
      replyTo ! ResponseVoid
    }
  }

  def revokePremiumAccess(accesses: Seq[String]): Unit = {
    val replyTo = sender
    persist(PremiumAccessRevoked(Instant.now, accesses)) { evt ⇒
      commit(evt)
      log.info("premium access of: {} was taken from bot with id: {}", accesses, botUserId)
      replyTo ! ResponseVoid
    }
  }

}