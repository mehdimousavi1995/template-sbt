package http

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import spray.json._

import scala.util.{Failure, Success, Try}

trait HttpResponseGenerator {
  this: HttpServiceRoutes =>

  def generateHttpResponse[T](requestType: String)(implicit jsonFormatter: RootJsonFormat[T]): PartialFunction[Try[Either[HttpError, T]], StandardRoute] = {
    case Success(result) ⇒
      complete {
        result match {
          case Right(resp) ⇒
            HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, resp.toJson.toString))
          case Left(error) ⇒
            HttpResponse(StatusCodes.getForKey(error.code).getOrElse(StatusCodes.InternalServerError), entity = HttpEntity(error.error))
        }

      }
    case Failure(e) ⇒
      log.error("Failed to handle http request, caused by: {}", e)
      complete(HttpResponse(
        StatusCodes.InternalServerError,
        entity = HttpEntity("Internal Server Error!")
      ))
  }
}
