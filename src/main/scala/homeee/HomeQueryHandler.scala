package homeee

import messages.homeee.homessages.HomeQuries.{GetAllDevices, GetAllDevicesResponse, GetDevice, GetDeviceResponse, GetDeviceStatus, GetDeviceStatusResponse, GetHome, GetHomeResponse, GetRunningOnHost, GetRunningOnHostResponse}
import messages.homeee.homessages.{AllDevices, HeaterCoolerState, OnOrOffStatus}
import main.Constant._

import scala.concurrent.Future

private trait HomeQueryHandler extends DeviceHelper {
  this: HomeProcessor ⇒

  def getHome(gh: GetHome): Future[GetHomeResponse] = {
    Future.successful(GetHomeResponse(state.owner, state.devices, state.address, state.houseArea))
  }

  def getDeviceStatus(device: AllDevices): (String, Option[Int]) = device match {
    case AllDevices(d) if d.isDefined && d.isLampDevice =>
      (d.lampDevice.get.onOrOffStatus match {
        case OnOrOffStatus.OFF => OFF
        case OnOrOffStatus.ON => ON
        case _ => ""
      }, None)
    case AllDevices(d) if d.isDefined && d.isHeaterCooler =>
      val HeaterCooler = d.heaterCooler.get
      (HeaterCooler.heaterCoolerState match {
        case HeaterCoolerState.COOLER => COOLER
        case HeaterCoolerState.HEATER => HEATER
        case HeaterCoolerState.OFFLINE => OFF
      }, Some(HeaterCooler.temperature))
  }

  def getDevice(gd: GetDevice): Future[GetDeviceResponse] = {
    Future.successful(GetDeviceResponse(state.devices.getDevice(gd.deviceId)))
  }

  def getDeviceStatus(gds: GetDeviceStatus): Future[GetDeviceStatusResponse] = {
    val device = state.devices.filter {
      case AllDevices(d) if d.isDefined && d.isHeaterCooler =>
        d.heaterCooler.get.deviceId == gds.deviceId
      case AllDevices(d) if d.isDefined && d.isLampDevice =>
        d.lampDevice.get.deviceId == gds.deviceId
      case _ => false
    }
    if (device.nonEmpty) {
      val (status, optTemp) = getDeviceStatus(device.head)
      Future.successful(GetDeviceStatusResponse(status, optTemp))
    } else Future.failed(new RuntimeException("there is no device with id: " + gds.deviceId + "for home: " + gds.homeId))
  }


  def getAllDevices(q: GetAllDevices): Future[GetAllDevicesResponse] = {
    Future.successful(GetAllDevicesResponse(state.devices))
  }

  def getRunningHost(q: GetRunningOnHost): Future[GetRunningOnHostResponse] = {
    val nodeId = conf.getString("node-id-in-cluster")
    Future.successful(GetRunningOnHostResponse(nodeId))
  }
}