

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.stream.ActorMaterializer
import homeee.{HomeExtension, HomeProcessor}
import http.HttpServiceRoutes
import kafka.{KafkaDeviceStatusConsumer, KafkaExtension}
import persist.postgres.PostgresDBExtension
//import kafka.{KafkaConsumer, KafkaExtension}
import persist.redis.RedisExtension
import sdk.CustomConfig

import scala.concurrent.ExecutionContextExecutor

object Main extends App {

  val config = CustomConfig.load()
  implicit val system: ActorSystem = ActorSystem("template", config)
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  val log = system.log
  val redisExt = RedisExtension(system)
  val pdb = PostgresDBExtension(system).db

  val httpHandler = new HttpServiceRoutes()(system)
  httpHandler.start()

  if (config.getList("akka.cluster.seed-nodes").isEmpty)
    Cluster(system).join(Cluster(system).selfAddress)


  HomeProcessor.register()

  val botAccess = HomeExtension(system)
  botAccess.hp

  val consumer = KafkaDeviceStatusConsumer()(system)

  consumer.subscribe(Set("mehdi"))

  val kafkaExt = KafkaExtension(system).broker


  1 to 1000 foreach { _ =>
    Thread.sleep(500)
    kafkaExt.publish("mehdi", "key", "newMessage")
  }


}
