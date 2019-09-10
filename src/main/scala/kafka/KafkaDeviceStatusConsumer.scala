package kafka

import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage.CommittableMessage
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import homeee.HomeExtension
import http.JsonSerializer
import http.entities.DeviceStatusRequest
import org.apache.kafka.clients.consumer.ConsumerConfig

import scala.concurrent.{ExecutionContextExecutor, Future}


case class KafkaDeviceStatusConsumer()(implicit system: ActorSystem) extends JsonSerializer {

  val parallelLimit = 2
  val kafkaExt: KafkaManager = KafkaExtension(system).broker
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val mat: ActorMaterializer = ActorMaterializer()
  val homeExt = HomeExtension(system)

  val flow: CommittableMessage[String, String] ⇒ Future[(String, Option[Int])] = { msg: CommittableMessage[String, String] ⇒
    msg.record.value() match {
      case messageValue: String =>
        msg.committableOffset.commitScaladsl().flatMap { _ ⇒
          Future.successful((messageValue, Some(0)))
        }
    }
  }
  val config: Config = system.settings.config.getConfig("services.kafka")
  import spray.json._

  def subscribe(topics: Set[String], groupId: String = "group-id"): Unit = {

    Consumer.committableSource(
      kafkaExt.consumerSettings(groupId)
        .withBootstrapServers(config.getString("consumer.bootstrap.servers"))
        .withGroupId(groupId)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      ,
      Subscriptions.topics(topics)
    ).mapAsync(parallelLimit)(flow).runForeach {
      case (publishedMessage: String, _: Option[Int]) ⇒
        val statusRequest: DeviceStatusRequest = publishedMessage.parseJson.convertTo[DeviceStatusRequest]
        homeExt.deviceStatus(statusRequest.homeId, statusRequest.deviceId, statusRequest.status).map { _ =>
          system.log.info("published device status to kafka, homeId: {}, deviceId: {}, status: {}",
            statusRequest.homeId, statusRequest.deviceId, statusRequest.status)
        }
    }(mat)
  }

}
