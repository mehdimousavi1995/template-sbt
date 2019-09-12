package homeee

import messages.homeee.homessages.{AllDevices, HeaterCoolerState, OnOrOffStatus}

trait DeviceHelper {

  implicit class RichDevice(devices: Seq[AllDevices]) {
    def getDevice(deviceId: String): Option[AllDevices] = {
      devices.find {
        case AllDevices(d) if d.isDefined && d.isHeaterCooler =>
          d.heaterCooler.get.deviceId == deviceId
        case AllDevices(d) if d.isDefined && d.isLampDevice =>
          d.lampDevice.get.deviceId == deviceId
        case _ => false
      }
    }

    def removeDevice(deviceId: String): Seq[AllDevices] = {
      devices.filterNot {
        case AllDevices(d) if d.isDefined && d.isHeaterCooler =>
          d.heaterCooler.get.deviceId == deviceId
        case AllDevices(d) if d.isDefined && d.isLampDevice =>
          d.lampDevice.get.deviceId == deviceId
        case _ => false
      }
    }

    import main.Constant._
    def updateDevice(deviceId: String, status: String, optTemp: Option[Int]): Seq[AllDevices] = {
      devices.map {
        case AllDevices(d) if d.isDefined && d.isHeaterCooler && d.heaterCooler.get.deviceId == deviceId =>
          val device = d.heaterCooler.get
          val temp = optTemp.getOrElse(device.temperature)
          val updatedDevice = status match {
            case HEATER =>
              device.copy(heaterCoolerState = HeaterCoolerState.HEATER, temperature = temp)
            case COOLER =>
              device.copy(heaterCoolerState = HeaterCoolerState.COOLER, temperature = temp)
            case OFF =>
              device.copy(heaterCoolerState = HeaterCoolerState.OFFLINE, temperature = temp)
            case _ => device
          }

          AllDevices().withHeaterCooler(updatedDevice)

        case AllDevices(d) if d.isDefined && d.isLampDevice && d.lampDevice.get.deviceId == deviceId=>
          val device = d.lampDevice.get
          val updatedDevice = status match {
            case OFF if device.onOrOffStatus.isOn =>
              device.copy(onOrOffStatus = OnOrOffStatus.OFF)
            case ON if device.onOrOffStatus.isOff =>
              device.copy(onOrOffStatus = OnOrOffStatus.ON)
            case _ => device
          }
          AllDevices().withLampDevice(updatedDevice)
        case d => d
      }
    }

  }
}
