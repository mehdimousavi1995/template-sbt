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
  val homeId: Int
}

trait HomeQuery {
  val homeId: Int
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

  def persistenceIdFor(botId: Int): String = s"BotAccess-$botId"

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
  protected val botUserId: Int = self.path.name.toInt


  override def persistenceId: String = HomeProcessor.persistenceIdFor(botUserId)

  override protected def getInitialState: HomeState = HomeState.initial(botUserId)

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