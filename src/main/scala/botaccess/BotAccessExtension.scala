package botaccess

import akka.actor.{ActorRef, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import messages.botaccess.BotAccessQueries.{GetPremiumAccessResponse, GetRolesResponse, HasAccessResponse, HasPremiumAccessResponse}

import scala.concurrent.Future


trait BotAccessExtension extends Extension {

  val bap: ActorRef

  def grantAccess(userId: Int, roles: Seq[String]): Future[Any]

  def revokeAccess(userId: Int, roles: Seq[String]): Future[Any]

  def grantPremiumAccess(userId: Int, accesses: Seq[String]): Future[Any]

  def revokePremiumAccess(userId: Int, accesses: Seq[String]): Future[Any]

  def hasAccess(userId: Int, service: String, api: String): Future[HasAccessResponse]

  def hasPremiumAccess(userId: Int, service: String, api: String): Future[HasPremiumAccessResponse]

  def getRoles(userId: Int): Future[GetRolesResponse]

  def getPremiumAccesses(userId: Int): Future[GetPremiumAccessResponse]

}

object BotAccessExtension extends ExtensionId[BotAccessExtension] with ExtensionIdProvider {
  override def lookup = BotAccessExtension

  override def createExtension(system: ExtendedActorSystem) = new BotAccessExtensionImp(system)
}