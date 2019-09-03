package homeee

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import messages.homeee.homessages.HomeCommands.CreateHome
import messages.homeee.homessages.Owner

import scala.concurrent.{ExecutionContext, Future}

final class HomeExtensionImp(system: ActorSystem) extends HomeExtension {
  HomeProcessor.register()

  private implicit val s: ActorSystem = system
  private implicit val ec: ExecutionContext = system.dispatcher
  private implicit val timeout: Timeout = Timeout(60, TimeUnit.SECONDS)

  override val hp: ActorRef = HomeProcessorRegion.start().ref

  override def createHome(homeId: String, owner: Owner, address: String, houseArea: Int, createdAt: Long): Future[Any] =
    (hp ? CreateHome(homeId, owner, address, houseArea)).mapTo[Any]

}