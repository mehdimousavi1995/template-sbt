package homeee

import messages.homeee.homessages.{AllDevices, HeatingCoolerState, OnOrOffStatus}

trait DeviceHelper {

  implicit class RichDevice(devices: Seq[AllDevices]) {
    def getDevice(deviceId: String): Option[AllDevices] = {
      devices.find {
        case AllDevices(d) if d.isDefined && d.isHeatingCooler =>
          d.heatingCooler.get.deviceId == deviceId
        case AllDevices(d) if d.isDefined && d.isLampDevice =>
          d.lampDevice.get.deviceId == deviceId
        case _ => false
      }
    }

    def removeDevice(deviceId: String): Seq[AllDevices] = {
      devices.filterNot {
        case AllDevices(d) if d.isDefined && d.isHeatingCooler =>
          d.heatingCooler.get.deviceId == deviceId
        case AllDevices(d) if d.isDefined && d.isLampDevice =>
          d.lampDevice.get.deviceId == deviceId
        case _ => false
      }
    }

    def updateDevice(deviceId: String, status: String, optTemp: Option[Int]): Seq[AllDevices] = {
      devices.map {
        case AllDevices(d) if d.isDefined && d.isHeatingCooler && d.heatingCooler.get.deviceId == deviceId =>
          val device = d.heatingCooler.get
          val temp = optTemp.getOrElse(device.temperature)
          val updatedDevice = status match {
            case "HEATER" =>
              device.copy(heatingCoolerState = HeatingCoolerState.HEATING, temperature = temp)
            case "COOLER" =>
              device.copy(heatingCoolerState = HeatingCoolerState.COOLER, temperature = temp)
            case "OFF" =>
              device.copy(heatingCoolerState = HeatingCoolerState.OFFLINE, temperature = temp)
          }
          AllDevices().withHeatingCooler(updatedDevice)

        case AllDevices(d) if d.isDefined && d.isLampDevice && d.lampDevice.get.deviceId == deviceId=>
          val device = d.lampDevice.get
          val updatedDevice = status match {
            case "OFF" if device.onOrOffStatus.isOn =>
              device.copy(onOrOffStatus = OnOrOffStatus.OFF)
            case "ON" if device.onOrOffStatus.isOff =>
              device.copy(onOrOffStatus = OnOrOffStatus.ON)
            case _ => device
          }
          AllDevices().withLampDevice(updatedDevice)
        case d => d
      }
    }

  }
}
