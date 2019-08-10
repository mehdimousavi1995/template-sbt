package botaccess

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import messages.botaccess.BotAccessCommands.{GrantAccess, GrantPremiumAccess, RevokeAccess, RevokePremiumAccess}
import messages.botaccess.BotAccessQueries
import messages.botaccess.BotAccessQueries.{GetPremiumAccess, GetPremiumAccessResponse, GetRoles,
  GetRolesResponse, HasAccess, HasAccessResponse, HasPremiumAccess, HasPremiumAccessResponse}

import scala.concurrent.{ExecutionContext, Future}

final class BotAccessExtensionImp(system: ActorSystem) extends BotAccessExtension {
  BotAccessProcessor.register()

  private implicit val s: ActorSystem = system
  private implicit val ec: ExecutionContext = system.dispatcher
  private implicit val timeout: Timeout = Timeout(60, TimeUnit.SECONDS)

  override val bap: ActorRef = BotAccessProcessorRegion.start().ref

  override def grantAccess(userId: Int, roles: Seq[String]): Future[Any] =
    (bap ? GrantAccess(userId, roles)).mapTo[Any]

  override def grantPremiumAccess(userId: Int, accesses: Seq[String]): Future[Any] =
    (bap ? GrantPremiumAccess(userId, accesses)).mapTo[Any]

  override def revokeAccess(userId: Int, roles: Seq[String]): Future[Any] =
    (bap ? RevokeAccess(userId, roles)).mapTo[Any]

  override def revokePremiumAccess(userId: Int, accesses: Seq[String]): Future[Any] =
    (bap ? RevokePremiumAccess(userId, accesses)).mapTo[Any]

  override def hasAccess(userId: Int, service: String, api: String): Future[HasAccessResponse] =
    (bap ? HasAccess(userId, service, api)).mapTo[HasAccessResponse]

  override def hasPremiumAccess(userId: Int, service: String, api: String): Future[BotAccessQueries.HasPremiumAccessResponse] =
    (bap ? HasPremiumAccess(userId, service, api)).mapTo[HasPremiumAccessResponse]

  override def getRoles(userId: Int): Future[GetRolesResponse] =
    (bap ? GetRoles(userId)).mapTo[GetRolesResponse]

  override def getPremiumAccesses(userId: Int): Future[BotAccessQueries.GetPremiumAccessResponse] =
    (bap ? GetPremiumAccess(userId)).mapTo[GetPremiumAccessResponse]
}