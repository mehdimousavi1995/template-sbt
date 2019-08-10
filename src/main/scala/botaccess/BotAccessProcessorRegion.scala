package botaccess

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.client.ClusterClientReceptionist
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}

final case class BotAccessProcessorRegion(ref: ActorRef)

object BotAccessProcessorRegion {

  private def extractEntityId(system: ActorSystem): ShardRegion.ExtractEntityId = {
    {
      case c: BotAccessCommand ⇒ (c.userId.toString, c)
      case q: BotAccessQuery   ⇒ (q.userId.toString, q)
    }
  }

  private def extractShardId(system: ActorSystem): ShardRegion.ExtractShardId = {
    case c: BotAccessQuery   ⇒ (c.userId % 100).toString
    case q: BotAccessCommand ⇒ (q.userId % 100).toString
  }

  val typeName = "BotAccessProcessor"

  private def start(props: Props)(implicit system: ActorSystem): BotAccessProcessorRegion = {
    val region = ClusterSharding(system).start(
      typeName = typeName,
      entityProps = props,
      settings = ClusterShardingSettings(system),
      extractEntityId = extractEntityId(system),
      extractShardId = extractShardId(system)
    )
    ClusterClientReceptionist(system).registerService(ClusterSharding(system).shardRegion(typeName))
    BotAccessProcessorRegion(region)
  }

  def start()(implicit system: ActorSystem): BotAccessProcessorRegion =
    start(BotAccessProcessor.props)

  def startProxy()(implicit system: ActorSystem): BotAccessProcessorRegion =
    BotAccessProcessorRegion(ClusterSharding(system).startProxy(
      typeName = typeName,
      role = None,
      extractEntityId = extractEntityId(system),
      extractShardId = extractShardId(system)
    ))
}
