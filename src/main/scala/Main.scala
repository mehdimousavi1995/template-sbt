

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.stream.ActorMaterializer
import homeee.{HomeExtension, HomeProcessor}
import http.HttpServiceRoutes
import persist.cassandra.CassandraDatabaseProvider
import persist.cassandra.home.HomeService
import persist.cassandra.owner.OwnerService
import persist.postgres.PostgresDBExtension
import com.outworkers.phantom.dsl._
import kafka.KafkaDeviceStatusConsumer
import persist.cassandra.device.DeviceService
import persist.redis.RedisExtension
import sdk.CustomConfig

import scala.concurrent.ExecutionContextExecutor
import scala.language.reflectiveCalls

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
  val homeService = new HomeService with CassandraDatabaseProvider
  val ownerService = new OwnerService with CassandraDatabaseProvider
  val deviceService = new DeviceService with CassandraDatabaseProvider

  homeService.database.create()(ec)
  ownerService.database.create()(ec)
  deviceService.database.create()(ec)

  val botAccess = HomeExtension(system)
  botAccess.hp

  val deviceStatusTopic = "device-status-topic"
  val kafkaKey = "kafkaKey"
  val deviceConsumer = KafkaDeviceStatusConsumer()
  deviceConsumer.subscribe(Set(deviceStatusTopic))

}
