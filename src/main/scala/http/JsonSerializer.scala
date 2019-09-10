package http

import http.entities._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat


trait JsonSerializer extends HttpImplicitConversions {
  implicit val userRequestFormat: RootJsonFormat[UserRequest] = jsonFormat3(UserRequest)
  implicit val userCreateResponse: RootJsonFormat[SuccessMessage] = jsonFormat2(SuccessMessage)

  implicit val loginRequestFormat: RootJsonFormat[LoginRequest] = jsonFormat2(LoginRequest)
  implicit val loginResponseFormat: RootJsonFormat[LoginResponse] = jsonFormat2(LoginResponse)


  implicit val homeDTOFormat: RootJsonFormat[HomeDTO] = jsonFormat3(HomeDTO)
  implicit val ownerDTOFormat: RootJsonFormat[OwnerDTO] = jsonFormat3(OwnerDTO)
  implicit val deviceDTOFormat: RootJsonFormat[DeviceDTO] = jsonFormat3(DeviceDTO)
  implicit val responseFormat: RootJsonFormat[SuccessResponse] = jsonFormat1(SuccessResponse)

  implicit val deviceStatusRequestFormat: RootJsonFormat[DeviceStatusRequest] = jsonFormat4(DeviceStatusRequest)
  implicit val deviceStatusResponseFormat: RootJsonFormat[DeviceStatusResponse] = jsonFormat1(DeviceStatusResponse)

  implicit val getDeviceStatusRequestFormat: RootJsonFormat[GetDeviceStatusRequest] = jsonFormat2(GetDeviceStatusRequest)
  implicit val getDeviceStatusResponseFormat: RootJsonFormat[GetDeviceStatusResponse] = jsonFormat2(GetDeviceStatusResponse)



}
