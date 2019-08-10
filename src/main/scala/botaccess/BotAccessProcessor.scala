package botaccess

import java.time.Instant
import java.util.concurrent.TimeUnit

import akka.actor.{ActorLogging, ActorSystem, Props, Stash}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import cqrs.{IncrementalSnapshots, Processor, TaggedEvent}
import im.actor.serialization.ActorSerializer
import messages.botaccess.BotAccessCommands._
import messages.botaccess.BotAccessQueries._
import messages.botaccess._

import scala.concurrent.{ExecutionContext, Future}

trait BotAccessCommand {
  val userId: Int
}

trait BotAccessQuery {
  val userId: Int
}

trait BotAccessEvent extends TaggedEvent {
  val ts: Instant

  override def tags: Set[String] = Set("bot-access")
}

object BotAccessProcessor {

  def register(): Unit =
    ActorSerializer.register(
      85801 → classOf[BotAccessQueries.HasAccess],
      85802 → classOf[BotAccessQueries.HasAccessResponse],
      85803 → classOf[BotAccessQueries.GetRoles],
      85804 → classOf[BotAccessQueries.GetRolesResponse],
      85055 → classOf[BotAccessQueries.GetPremiumAccess],
      85056 → classOf[BotAccessQueries.GetPremiumAccessResponse],

      85850 → classOf[BotAccessCommands.GrantAccess],
      85851 → classOf[BotAccessCommands.RevokeAccess],
      85852 → classOf[BotAccessCommands.GrantPremiumAccess],
      85853 → classOf[BotAccessCommands.RevokePremiumAccess],

      85900 → classOf[BotAccessEvents.AccessGranted],
      85901 → classOf[BotAccessEvents.AccessRevoked],
      85902 → classOf[BotAccessEvents.PremiumAccessGranted],
      85903 → classOf[BotAccessEvents.PremiumAccessRevoked],

      85950 → classOf[Role],
      85951 → classOf[Access],
      85952 → classOf[BotAccessSnapShot]
    )

  def persistenceIdFor(botId: Int): String = s"BotAccess-$botId"

  def props: Props = Props(classOf[BotAccessProcessor])
}

private final class BotAccessProcessor
  extends Processor[BotAccessState]
  with IncrementalSnapshots[BotAccessState]
  with BotAccessQueryHandler
  with BotAccessCommandHandler
  with Stash
  with ActorLogging {

  private val conf = ConfigFactory.load()
  protected implicit val system: ActorSystem = context.system
  protected implicit val ec: ExecutionContext = context.dispatcher
  protected implicit val timeout: Timeout = Timeout(60, TimeUnit.SECONDS)
  val baseRoleConf = "services.bot.access."
  protected val botUserId: Int = self.path.name.toInt


  override def persistenceId: String = BotAccessProcessor.persistenceIdFor(botUserId)

  override protected def getInitialState: BotAccessState = BotAccessState.initial(botUserId)

  override protected def saveSnapshotIfNeeded(): Unit = {
    super.saveSnapshotIfNeeded()
  }

  override protected def handleCommand: Receive = {
    case GrantAccess(_, roles)            ⇒ grantAccess(roles)
    case RevokeAccess(_, roles)           ⇒ revokeAccess(roles)
    case GrantPremiumAccess(_, accesses)  ⇒ grantPremiumAccess(accesses)
    case RevokePremiumAccess(_, accesses) ⇒ revokePremiumAccess(accesses)
  }

  override protected def handleQuery: PartialFunction[Any, Future[Any]] = {
    case GetRoles(_)                       ⇒ getRoles.map(GetRolesResponse(_))
    case HasAccess(_, service, api)        ⇒ hasAccess(service, api).map(HasAccessResponse(_))
    case HasPremiumAccess(_, service, api) ⇒ hasPremiumAccess(service, api).map(HasPremiumAccessResponse(_))
    case GetPremiumAccess(_)               ⇒ getPremiumAccess.map(GetPremiumAccessResponse(_))
  }

}