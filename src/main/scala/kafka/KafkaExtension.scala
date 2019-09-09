package kafka

import akka.actor.{ ActorSystem, ExtendedActorSystem, Extension, ExtensionId }

class KafkaExtensionImpl(system: ActorSystem) extends Extension {
  val broker = new KafkaManager(system, system.settings.config.getConfig("services.kafka"))
}

object KafkaExtension extends ExtensionId[KafkaExtensionImpl] {
  override def createExtension(system: ExtendedActorSystem): KafkaExtensionImpl = new KafkaExtensionImpl(system)
}