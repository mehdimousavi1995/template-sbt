package http

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import http.entities.{LoginRequest, UserRequest}
import org.bouncycastle.util.encoders.Base64
import persist.postgres.PostgresDBExtension
import persist.redis.RedisExtension
import util.AuthenticationHelper

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.Try

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
  

  protected val log: LoggingAdapter = system.log
  protected val pdb = PostgresDBExtension(system).db
  protected val redisExt = RedisExtension(system)


  override def routes: Route = extractClientIP { ip =>
    path("users") {
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
        extractCredentials { credentials =>
          val optToken = Try(credentials.map(x ⇒ new String(Base64.decode(x.token())).split(":")(1))).getOrElse(Some(""))
          authenticateBasicAsync(realm = "secure site", cre => authenticator(optToken, cre, ip)) { username =>
            complete("thank you")
          }
        }
      }
    }
  }

  def start(): Future[Http.ServerBinding] = {
    Http().bindAndHandle(routes, host, port)
  }

}
