import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.typesafe.config.{Config, ConfigFactory}
import http.entities.LoginRequest
import http.{HttpServiceRoutes, JsonSerializer}
import org.scalatest.{Matchers, WordSpec}
import sdk.CustomConfig

class LoginSpec extends WordSpec with Matchers with ScalatestRouteTest with JsonSerializer {
  override protected def createActorSystem(): ActorSystem = {
    val defaults: Config = ConfigFactory.empty()
    val config: Config = CustomConfig.load()
    ActorSystem("template", config)
  }
  val httpHandler = new HttpServiceRoutes()

  import spray.json._

  val loginRequest = ByteString(LoginRequest("ali", "ali").toJson.toString())

  "Login service" should {
    "verify username and password" in {
      Post("/auths").withEntity(ContentTypes.`application/json`, loginRequest) ~> httpHandler.routes ~> check {
        println(responseEntity)
        status shouldEqual StatusCodes.Unauthorized
      }
    }
  }

}
