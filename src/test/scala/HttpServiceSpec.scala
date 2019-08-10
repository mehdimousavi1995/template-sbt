import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.util.ByteString
import http.{JsonSerializer, _}
import spray.json._

class HttpServiceSpec extends BaseAppSuite with JsonSerializer {

  val httpHandler = new HttpServiceRoutes()


}



