package http.entities

import java.util.UUID

final case class DeviceDTO(
                         deviceName: String,
                         deviceType: String,
                         homeId: UUID
                       )

final case class CreateDeviceDTO(devices: List[DeviceDTO])