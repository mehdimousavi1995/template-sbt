package kafka

import akka.actor.ActorSystem
import akka.event.LogMarker
object KafkaProcessor {
  final val TAG = LogMarker("kafka")
  def register(): Unit = {
  }
  @Deprecated
  def prepareTopic(topic: String, nPartition: Int, nReplicaiton: Int)(implicit system: ActorSystem): Unit = {
  }
}
