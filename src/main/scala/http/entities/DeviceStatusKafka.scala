package http.entities

import java.util.UUID


case class DeviceStatusRequest(
                                deviceId: UUID,
                                homeId: UUID,
                                status: String,
                                optTemp: Option[Int] = None) {
  require(
    (optTemp.isDefined && (status == "OFF" || status == "HEATER" || status == "COOLER")) ||
      (optTemp.isEmpty && (status == "OFF" || status == "ON")),
    "INVALID_INPUT_BY_CLIENT"
  )
}

case class DeviceStatusResponse(message: String = "SUCCESS")


case class GetDeviceStatusResponse(
                                    status: String,
                                    optTemp: Option[Int] = None
                                  )
