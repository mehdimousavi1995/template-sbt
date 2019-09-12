package http

import http.entities._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat


trait JsonSerializer extends HttpImplicitConversions {
  implicit val userRequestFormat: RootJsonFormat[UserRequest] = jsonFormat3(UserRequest)
  implicit val userCreateResponse: RootJsonFormat[SuccessMessage] = jsonFormat2(SuccessMessage)

  implicit val loginRequestFormat: RootJsonFormat[LoginRequest] = jsonFormat2(LoginRequest)
  implicit val loginResponseFormat: RootJsonFormat[LoginResponse] = jsonFormat2(LoginResponse)


  implicit val homeDTOFormat: RootJsonFormat[CreateHomeDTO] = jsonFormat3(CreateHomeDTO)
  implicit val homeResponseDTOFormat: RootJsonFormat[CreateHomeResponseDTO] = jsonFormat4(CreateHomeResponseDTO)
  implicit val ownerDTOFormat: RootJsonFormat[CreateOwnerDTO] = jsonFormat3(CreateOwnerDTO)
  implicit val ownerResponseDTOFormat: RootJsonFormat[CreateOwnerResponseDTO] = jsonFormat4(CreateOwnerResponseDTO)
  implicit val deviceDTOFormat: RootJsonFormat[DeviceDTO] = jsonFormat3(DeviceDTO)
  implicit val deviceResponseDTOFormat: RootJsonFormat[DeviceResponseDTO] = jsonFormat4(DeviceResponseDTO)
  implicit val responseFormat: RootJsonFormat[SuccessResponse] = jsonFormat1(SuccessResponse)

  implicit val deviceStatusRequestFormat: RootJsonFormat[DeviceStatusDTO] = jsonFormat4(DeviceStatusDTO)
  implicit val deviceStatusResponseFormat: RootJsonFormat[DeviceStatusResponse] = jsonFormat1(DeviceStatusResponse)

  implicit val getDeviceStatusResponseFormat: RootJsonFormat[GetDeviceStatusResponse] = jsonFormat2(GetDeviceStatusResponse)

  implicit val deviceInfoResponseDTOFormat: RootJsonFormat[DeviceInfoDTO] = jsonFormat5(DeviceInfoDTO)
  implicit val getAllDeviceResponseDTOFormat: RootJsonFormat[GetAllDeviceResponseDTO] = jsonFormat1(GetAllDeviceResponseDTO)


}
