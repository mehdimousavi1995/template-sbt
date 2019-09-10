package homeee

import messages.homeee.homessages.AllDevices

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
        // TODO remember to update device state
        case s@AllDevices(d) if d.isDefined && d.isHeatingCooler =>
          s
        case s@AllDevices(d) if d.isDefined && d.isLampDevice =>
          s
        case d => d
      }
    }

  }
}
