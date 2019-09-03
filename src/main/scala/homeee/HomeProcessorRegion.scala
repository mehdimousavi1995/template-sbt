package homeee

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.client.ClusterClientReceptionist
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}

final case class HomeProcessorRegion(ref: ActorRef)

object HomeProcessorRegion {

  private def extractEntityId(system: ActorSystem): ShardRegion.ExtractEntityId = {
    {
      case h: HomeCommand ⇒ (h.homeId, h)
      case q: HomeQuery   ⇒ (q.homeId, q)
    }
  }

  private def extractShardId(system: ActorSystem): ShardRegion.ExtractShardId = {
    case h: HomeCommand   ⇒ (h.homeId.hashCode % 100).toString
    case q: HomeQuery ⇒ (q.homeId.hashCode % 100).toString
  }

  val typeName = "BotAccessProcessor"

  private def start(props: Props)(implicit system: ActorSystem): HomeProcessorRegion = {
    val region = ClusterSharding(system).start(
      typeName = typeName,
      entityProps = props,
      settings = ClusterShardingSettings(system),
      extractEntityId = extractEntityId(system),
      extractShardId = extractShardId(system)
    )
    ClusterClientReceptionist(system).registerService(ClusterSharding(system).shardRegion(typeName))
    HomeProcessorRegion(region)
  }

  def start()(implicit system: ActorSystem): HomeProcessorRegion =
    start(HomeProcessor.props)

  def startProxy()(implicit system: ActorSystem): HomeProcessorRegion =
    HomeProcessorRegion(ClusterSharding(system).startProxy(
      typeName = typeName,
      role = None,
      extractEntityId = extractEntityId(system),
      extractShardId = extractShardId(system)
    ))
}
