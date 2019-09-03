package homeee

import akka.actor.{ActorRef, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import messages.homeee.homessages.Owner

import scala.concurrent.Future


trait HomeExtension extends Extension {

  val hp: ActorRef

  def createHome(homeId: String, owner: Owner, address: String, houseArea: Int, createdAt: Long): Future[Any]


}

object HomeExtension extends ExtensionId[HomeExtension] with ExtensionIdProvider {
  override def lookup = HomeExtension

  override def createExtension(system: ExtendedActorSystem) = new HomeExtensionImp(system)
}