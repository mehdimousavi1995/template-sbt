package http

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import akka.stream.ActorMaterializer
import homeee.HomeExtension
import http.entities._
import persist.cassandra.device.DeviceService
import persist.cassandra.home.HomeService
import persist.cassandra.owner.OwnerService
import persist.cassandra.{AppDatabase, AppDatabaseProvider, CassandraConnection, CassandraDatabaseProvider}
import persist.postgres.PostgresDBExtension
import persist.redis.RedisExtension
import util.AuthenticationHelper

import scala.concurrent.{ExecutionContextExecutor, Future}

class HttpServiceRoutes()(implicit val system: ActorSystem) extends HttpHandler
  with HttpImplicitConversions
  with JsonSerializer
  with HttpRequestHandler
  with HttpResponseGenerator
  with AuthenticationHelper {

  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  val host: String = system.settings.config.getString("http.listen-address.host")
  val port: Int = system.settings.config.getInt("http.listen-address.port")


  val partitionKey = "partition-1"
  val homeService = new HomeService with CassandraDatabaseProvider
  val ownerService = new OwnerService with CassandraDatabaseProvider
  val deviceService = new DeviceService with CassandraDatabaseProvider

  val homeExt = HomeExtension(system)

  protected val log: LoggingAdapter = system.log
  protected val pdb = PostgresDBExtension(system).db
  protected val redisExt = RedisExtension(system)


  override def routes: Route = extractClientIP { ip =>

    path("houses") {
      post {
        entity(as[CreateHomeDTO]) { request =>
          onComplete(createHome(request)) {
            generateHttpResponse("create home")
          }
        }
      }
    } ~ path("owners") {
      post {
        entity(as[CreateOwnerDTO]) { request =>
          onComplete(createOwner(request)) {
            generateHttpResponse("create owner")
          }
        }
      }
    } ~ path("devices") {
      post {
        entity(as[DeviceDTO]) { request =>
          onComplete(createDevices(request)) {
            generateHttpResponse("crated devices")
          }
        }
      }

    } ~ path("users") {
      post {
        entity(as[UserRequest]) { request =>
          onComplete(createUser(request)) {
            generateHttpResponse("create user")
          }
        }
      }
    } ~ path("auths") {
      post {
        entity(as[LoginRequest]) { request =>
          onComplete(login(request)) {
            generateHttpResponse("login")
          }
        }
      }
    } ~ path("status") {
      post {
        entity(as[DeviceStatusRequest]) { request =>
          onComplete(publishStatusToKafka(request)) {
            generateHttpResponse("status")
          }
        }
      }
    } ~ path("homes" / Segment / "devices" / Segment) { (homeId, deviceId) =>
      get {
        onComplete(getStatus(homeId, deviceId)) {
          generateHttpResponse("get status")
        }
      }
    }
  }

  def oAuthAuthenticator(credentials: Credentials): Future[Option[String]] =
    credentials match {
      case p@Credentials.Provided(identifier) =>
        redisExt.get(identifier).map { value =>
          if (p.verify(value.getOrElse("")))
            Some(identifier)
          else None
        }
      case _ => Future.successful(None)
    }

  def start(): Future[Http.ServerBinding] = {
    Http().bindAndHandle(routes, host, port)
  }

}
