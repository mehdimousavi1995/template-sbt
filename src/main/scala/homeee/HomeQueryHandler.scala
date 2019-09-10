package homeee

import http.entities.GetDeviceStatusResponse
import messages.homeee.homessages.{AllDevices, HeatingCoolerState, OnOrOffStatus}
import messages.homeee.homessages.HomeQuries.{GetDevice, GetDeviceResponse, GetDeviceSatatus, GetHome, GetHomeResponse}

import scala.concurrent.Future

private trait HomeQueryHandler extends DeviceHelper {
  this: HomeProcessor ⇒

  def getHome(gh: GetHome): Future[GetHomeResponse] = {
    Future.successful(GetHomeResponse(state.owner, state.devices, state.address, state.houseArea))
  }

  def getDeviceStatus(device: AllDevices): (String, Option[Int]) = device match {
    case AllDevices(d) if d.isDefined && d.isLampDevice =>
      (d.lampDevice.get.onOrOffStatus match {
        case OnOrOffStatus.BROKEN => "BROKEN"
        case OnOrOffStatus.OFF => "OFF"
        case OnOrOffStatus.ON => "ON"
        case _ => ""
      }, None)
    case AllDevices(d) if d.isDefined && d.isHeatingCooler =>
      val heatingCooler = d.heatingCooler.get
      (heatingCooler.heatingCoolerState match {
        case HeatingCoolerState.COOLER => "COOLER"
        case HeatingCoolerState.HEATING => "HEATING"
        case HeatingCoolerState.OFFLINE => "OFF"
      }, Some(heatingCooler.temperature))
  }

  def getDevice(gd: GetDevice): Future[GetDeviceResponse] = {
    Future.successful(GetDeviceResponse(state.devices.getDevice(gd.deviceId)))
  }

  def getDeviceStatus(gds: GetDeviceSatatus): Future[GetDeviceStatusResponse] = {
    val device = state.devices.filter {
      case AllDevices(d) if d.isDefined && d.isHeatingCooler =>
        d.heatingCooler.get.deviceId == gds.deviceId
      case AllDevices(d) if d.isDefined && d.isLampDevice =>
        d.lampDevice.get.deviceId == gds.deviceId
      case _ => false
    }
    if (device.nonEmpty) {
      val (status, optTemp) = getDeviceStatus(device.head)
      Future.successful(GetDeviceStatusResponse(status, optTemp))
    } else Future.failed(new RuntimeException("there is no device with id: " + gds.deviceId + "for home: " + gds.homeId))
  }

}