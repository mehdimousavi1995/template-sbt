package http.entities


case class DeviceStatusRequest(deviceId: String,
                              homeId: String,
                              status: String,
                              optTemp: Option[Int] = None)

case class DeviceStatusResponse(message: String = "SUCCESS")



case class GetDeviceStatusRequest(
                                 deviceId: String,
                                 homeId: String
                                 )

case class GetDeviceStatusResponse(
                                  status: String,
                                  optTemp: Option[Int] = None
                                  )
