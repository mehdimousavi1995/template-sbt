package http

import http.entities._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat


trait JsonSerializer extends HttpImplicitConversions {
  implicit val userRequestFormat: RootJsonFormat[UserRequest] = jsonFormat3(UserRequest)
  implicit val userCreateResponse: RootJsonFormat[SuccessMessage] = jsonFormat2(SuccessMessage)

  implicit val loginRequestFormat: RootJsonFormat[LoginRequest] = jsonFormat2(LoginRequest)
  implicit val loginResponseFormat: RootJsonFormat[LoginResponse] = jsonFormat2(LoginResponse)

}
