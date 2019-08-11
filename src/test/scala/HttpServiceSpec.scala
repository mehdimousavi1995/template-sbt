import akka.http.javadsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials, OAuth2BearerToken}
import akka.http.scaladsl.model.{ContentTypes, HttpHeader, StatusCodes}
import akka.util.ByteString
import http.{JsonSerializer, _}
import spray.json._

class HttpServiceSpec extends BaseAppSuite with JsonSerializer {

  val httpHandler = new HttpServiceRoutes()
  val header = Authorization(OAuth2BearerToken("asghar"))
  val auth: HttpHeader =
  "Short link service" should {
    "check authorization" in {
      Post("").withHeadersAndEntity(
        headers = List(Authorization(BasicHttpCredentials("", "")))
      ) ~> httpHandler.routes
    }
  }

}



