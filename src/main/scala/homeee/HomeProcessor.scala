package homeee

import java.security.acl.Owner
import java.time.Instant
import java.util.concurrent.TimeUnit

import akka.actor.{ActorLogging, ActorSystem, Props, Stash}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import cqrs.{IncrementalSnapshots, Processor, TaggedEvent}
import im.actor.serialization.ActorSerializer
import messages.homeee.homessages.HomeCommands.CreateHome
import messages.homeee.homessages.{Device, HomeCommands, HomeEvents, HomeSnapShot}

import scala.concurrent.{ExecutionContext, Future}

trait HomeCommand {
  val homeId: String
}

trait HomeQuery {
  val homeId: String
}

trait HomeEvent extends TaggedEvent {
  val ts: Instant

  override def tags: Set[String] = Set("home-event")
}

object HomeProcessor {

  def register(): Unit =
    ActorSerializer.register(

      85850 → classOf[HomeCommands.CreateHome],
      85851 → classOf[HomeCommands.CreatedHomeResponse],
      85852 → classOf[HomeEvents.HomeCreated],

      85950 → classOf[Owner],
      85951 → classOf[Device],
      85952 → classOf[HomeSnapShot]
    )

  def persistenceIdFor(homeId: String): String = s"Home-$homeId"

  def props: Props = Props(classOf[HomeProcessor])
}

private final class HomeProcessor
  extends Processor[HomeState]
    with IncrementalSnapshots[HomeState]
    with HomeQueryHandler
    with HomeCommandHandler
    with Stash
    with ActorLogging {

  private val conf = ConfigFactory.load()
  protected implicit val system: ActorSystem = context.system
  protected implicit val ec: ExecutionContext = context.dispatcher
  protected implicit val timeout: Timeout = Timeout(60, TimeUnit.SECONDS)
  val baseRoleConf = "services.bot.access."
  protected val homeUserId: String = self.path.name


  override def persistenceId: String = HomeProcessor.persistenceIdFor(homeUserId)

  override protected def getInitialState: HomeState = HomeState.initial(homeUserId)

  override protected def saveSnapshotIfNeeded(): Unit = {
    super.saveSnapshotIfNeeded()
  }

  override protected def handleCommand: Receive = {
    case c: CreateHome ⇒ createHome(c)
  }

  override protected def handleQuery: PartialFunction[Any, Future[Any]] = {
    case _ => Future.successful(Unit)

  }

}