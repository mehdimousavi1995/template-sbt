package kafka

import akka.actor.{ ActorRef, ActorSystem }
import akka.kafka.{ ConsumerSettings, KafkaConsumerActor, ProducerMessage }
import akka.pattern.ask
import akka.stream.QueueOfferResult
import akka.util.Timeout
import cakesolutions.kafka.KafkaProducer
import cakesolutions.kafka.KafkaProducer.Conf
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{ ProducerRecord, RecordMetadata }
import org.apache.kafka.common.serialization.{ Serializer, StringDeserializer, StringSerializer }

import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.util.{ Failure, Success }
import scalapb.GeneratedMessage

class KafkaManager(system: ActorSystem, config: Config, serializer: StringSerializer = new StringSerializer) {
  import scala.concurrent.duration._

  private implicit val _sys = system
  private implicit val timeout = Timeout(30.seconds)

  private lazy val ref: ActorRef = system.actorOf(KafkaProducerActor.props(config.getConfig("producer"), serializer))

  private implicit val ec: ExecutionContext = system.dispatchers.lookup(config.getString("producer.use-dispatcher"))

  private val producer = KafkaProducer(
    Conf(
      keySerializer = new StringSerializer(),
      valueSerializer = serializer,
      bootstrapServers = config.getString("producer.bootstrap.servers")).withConf(config.getConfig("producer.kafka-clients")))

  def actorOfConsumer(groupId: String, name: String)(implicit context: ActorSystem): ActorRef = {
    context.actorOf(KafkaConsumerActor.props(consumerSettings(groupId)), name)
  }

  def consumerSettings(groupId: String) = ConsumerSettings(
    config.getConfig("consumer"),
    new StringDeserializer,
    new StringDeserializer)
    .withBootstrapServers(config.getString("consumer.bootstrap.servers"))
    .withGroupId(groupId)
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  KafkaProcessor.register()

  def publish(topic: String, key: String, message: String): Future[QueueOfferResult] =
    publish(topic, key, message, Promise[Unit]())

  def publish(topic: String, key: String, message: String, promise: Promise[Unit]): Future[QueueOfferResult] =
    (ref ? new ProducerMessage.Message[String, String, Promise[Unit]](new ProducerRecord(topic, key, message), promise)).mapTo[QueueOfferResult]

  def send(topic: String, key: String, message: String): Future[RecordMetadata] =
    scala.util.Try(producer.send(new ProducerRecord[String, String](topic, message))) match {
      case Success(rsp) ⇒ rsp
      case Failure(e)   ⇒ Future.failed(e)
    }

  def send(topic: String, message: String): Future[RecordMetadata] =
    send(topic, null, message)

  def fromOption[A](x: Option[Future[A]])(implicit ec: ExecutionContext): Future[Option[A]] =
    x match {
      case Some(f) ⇒ f.map(Some(_))
      case None    ⇒ Future.successful(None)
    }

}
