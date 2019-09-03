
import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.stream.ActorMaterializer
import homeee.{HomeExtension, HomeProcessor}
import http.HttpServiceRoutes
import it.sauronsoftware.cron4j.Scheduler
import persist.postgres.PostgresDBExtension
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

  val pattern = config.getString("schedule-pattern")

  val s = new Scheduler()
  s.schedule(pattern, new Runnable {
    override def run(): Unit = {
      println("hi")
    }
  })
  s.start()

  HomeProcessor.register()

  val botAccess = HomeExtension(system)
  botAccess.bap


}
