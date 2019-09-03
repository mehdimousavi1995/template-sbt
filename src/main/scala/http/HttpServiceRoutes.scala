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
import http.entities.{HomeDTO, LoginRequest, UserRequest}
import persist.cassandra.home.HomeService
import persist.cassandra.{AppDatabase, AppDatabaseProvider, CassandraConnection}
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

  object CassandraDatabase extends AppDatabase(CassandraConnection.connection)

  trait CassandraDatabaseProvider extends AppDatabaseProvider {
    override def database: AppDatabase = CassandraDatabase
  }

  val partitionKey = "partition-1"
  val homeService = new HomeService with CassandraDatabaseProvider

  val homeExt = HomeExtension(system)

  protected val log: LoggingAdapter = system.log
  protected val pdb = PostgresDBExtension(system).db
  protected val redisExt = RedisExtension(system)


  override def routes: Route = extractClientIP { ip =>

    path("houses") {
      post {
        entity(as[HomeDTO]) { request =>
          onComplete(createHome(request)) {
            generateHttpResponse("create home")
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
    } ~ path("test") {
      // TODO remeber to change this api
      get {
        authenticateOAuth2Async(realm = "api", oAuthAuthenticator) { validToken =>
          complete(s"It worked! user = $validToken")
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
