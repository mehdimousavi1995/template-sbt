package persist.cassandra.device

import java.util.UUID

import util.TimeUtils
import util.TimeUtils._
import main.Constant._

final case class Device(
                         deviceId: UUID = UUID.randomUUID(),
                         deviceName: String,
                         deviceType: String,
                         homeId: UUID,
                         createdAt: Long = TimeUtils.nowTehran.toEpochMilliTehran
                       ) {
  require(deviceType == LAMP || deviceType == HEATER_COOLER)
}