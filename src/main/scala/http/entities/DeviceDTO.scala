package http.entities

import java.util.UUID
import main.Constant._

final case class DeviceDTO(
                         deviceName: String,
                         deviceType: String,
                         homeId: UUID
                       ) {
  require(deviceType == LAMP || deviceType == HEATER_COOLER)
}

final case class DeviceResponseDTO(
                                  deviceId: UUID,
                                  homeId: UUID,
                                  deviceName: String,
                                  deviceType: String
                                  )


final case class DeviceInfoDTO(
                              deviceId: String,
                              deviceName: String,
                              deviceType: String,
                              status: String,
                              optTemp: Option[Int]
                              )
final case class GetAllDeviceResponseDTO(allDevices: Seq[DeviceInfoDTO])