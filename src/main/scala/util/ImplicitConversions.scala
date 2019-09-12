package util

import http.entities.DeviceInfoDTO
import main.Constant._
import messages.homeee.homessages.{AllDevices, HeaterCoolerState, OnOrOffStatus}

trait ImplicitConversions {
  implicit def toOwner(owner: persist.cassandra.owner.Owner): messages.homeee.homessages.Owner =
    messages.homeee.homessages.Owner(owner.ownerId.toString, owner.firstName, owner.lastName, owner.telegramUserId)

  implicit def allDevicesToDeviceStatus(allDevices: AllDevices): DeviceInfoDTO = allDevices match {
    case AllDevices(d) if d.isDefined && d.isLampDevice =>
      val lamp = d.lampDevice.get
      DeviceInfoDTO(lamp.deviceId, lamp.deviceName, LAMP, lamp.onOrOffStatus match {
        case OnOrOffStatus.ON => ON
        case OnOrOffStatus.OFF => OFF
        case _ => OFF
      }, None)
    case AllDevices(d) if d.isDefined && d.isHeaterCooler =>
      val heaterCooler = d.heaterCooler.get
      DeviceInfoDTO(heaterCooler.deviceId, heaterCooler.deviceName, HEATER_COOLER, heaterCooler.heaterCoolerState match {
        case HeaterCoolerState.HEATER => HEATER
        case HeaterCoolerState.COOLER => COOLER
        case HeaterCoolerState.OFFLINE => OFF
        case _ => OFF
      }, Some(heaterCooler.temperature))
  }

  implicit def allDevicesToDeviceStatus(allDevices: Seq[AllDevices]): Seq[DeviceInfoDTO] =
    allDevices map allDevicesToDeviceStatus

}
