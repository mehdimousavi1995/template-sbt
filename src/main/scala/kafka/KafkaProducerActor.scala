package kafka

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.kafka.scaladsl.Producer
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.pattern.pipe
import akka.stream.QueueOfferResult.{Dropped, Enqueued, Failure, QueueClosed}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Materializer}
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}

import scala.concurrent.Promise

object KafkaProducerActor {

  object ProducerRecord {
    def apply(topic: String, key: String, message: String) =
      new ProducerRecord[String, String](topic, key, message)

    def unapply(arg: ProducerRecord[String, String]): Option[(String, String, String)] = Some((arg.topic(), arg.key(), arg.value()))
  }

  case object Restart

  def props(config: Config, serializer: Serializer[String] = new StringSerializer) = Props(new KafkaProducerActor(config, serializer)).withDispatcher(config.getString("use-dispatcher"))
}

class KafkaProducerActor(config: Config, serializer: Serializer[String] = new StringSerializer) extends Actor {

  import context.dispatcher

  private val log = Logging.withMarker(this)

  private implicit val mat: Materializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  private val producerSettings = ProducerSettings(
    config,
    new StringSerializer,
    serializer)
    .withBootstrapServers(config.getString("bootstrap.servers"))
  private val kafkaProducer = producerSettings.createKafkaProducer()
  private val bufferSize = config.getInt("buffer-size")
  private val overflowStrategy = akka.stream.OverflowStrategy.dropNew

  private val queue = Source.queue(bufferSize, overflowStrategy)
    .via(Producer.flow[String, String, Promise[Unit]](producerSettings, kafkaProducer))
    .to(Sink.foreach(_.message.passThrough.success(())))
    .run()

  queue watchCompletion () onComplete { r ⇒
    self ! KafkaProducerActor.Restart
  }

  override def receive: Receive = {
    case pr: ProducerMessage.Message[String, String, Promise[Unit]] ⇒
      val result = queue offer pr

      result foreach {
        case Dropped        ⇒ log.error(KafkaProcessor.TAG, "Message was dropped : {}", pr.record)
        case Failure(cause) ⇒ log.error(KafkaProcessor.TAG, cause, "Publish was failed {}", pr.record)
        case QueueClosed    ⇒ log.error(KafkaProcessor.TAG, "Queue is closed. Message will not publish : {}", pr.record)
        case Enqueued       ⇒
      }

      result.failed foreach {
        case cause: Throwable ⇒ log.error(KafkaProcessor.TAG, cause, "Error in offer to queue {}", pr.record)
      }

      result pipeTo sender()

    case KafkaProducerActor.Restart ⇒
      throw new RuntimeException("Restarting kafka actor because of completing queue stream")
  }

  override def postStop(): Unit = {
    kafkaProducer.close()
    super.postStop()
  }

}
