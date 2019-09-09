package kafka

import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage.CommittableMessage
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.{ExecutionContextExecutor, Future}


case class KafkaConsumer()(implicit system: ActorSystem) {

  val parallelLimit = 2
  val kafkaExt: KafkaManager = KafkaExtension(system).broker
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val flow: CommittableMessage[String, String] ⇒ Future[(String, Option[Int])] = { msg: CommittableMessage[String, String] ⇒
    msg.record.value() match {
      case messageValue: String =>
        msg.committableOffset.commitScaladsl().flatMap { _ ⇒
          Future.successful((messageValue, Some(0)))
        }
    }
  }
  val config: Config = system.settings.config.getConfig("services.kafka")

  def subscribe(topics: Set[String], groupId: String = "group-id"): Unit = {

    Consumer.committableSource(
      kafkaExt.consumerSettings(groupId)
        .withBootstrapServers(config.getString("consumer.bootstrap.servers"))
        .withGroupId(groupId)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      ,
      Subscriptions.topics(topics)
    ).mapAsync(parallelLimit)(flow).runForeach(res ⇒ {
      println("===========================================")
      println(res)
      println("===========================================")
    }
    )(mat)
  }

}
