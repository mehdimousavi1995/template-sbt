package http.entities

import java.util.UUID

final case class DeviceDTO(
                         deviceName: String,
                         deviceType: String,
                         homeId: UUID
                       ) {
  require(deviceType == "LAMP" || deviceType == "HEATER_COOLER")
}

final case class DeviceResponseDTO(
                                  deviceId: UUID,
                                  homeId: UUID,
                                  deviceName: String,
                                  deviceType: String
                                  )
