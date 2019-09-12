package homeee

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import messages.homeee.homessages.HomeCommands.{AddDevice, CreateHome, DeviceStatus, RemoveDevice}
import messages.homeee.homessages.HomeQuries.{GetAllDevices, GetAllDevicesResponse, GetDevice, GetDeviceResponse, GetDeviceStatus, GetDeviceStatusResponse, GetHome, GetHomeResponse}
import messages.homeee.homessages.{AllDevices, Owner}

import scala.concurrent.{ExecutionContext, Future}

final class HomeExtensionImp(system: ActorSystem) extends HomeExtension {
  HomeProcessor.register()

  private implicit val s: ActorSystem = system
  private implicit val ec: ExecutionContext = system.dispatcher
  private implicit val timeout: Timeout = Timeout(60, TimeUnit.SECONDS)

  override val hp: ActorRef = HomeProcessorRegion.start().ref

  override def createHome(homeId: String, owner: Owner, address: String, houseArea: Int, createdAt: Long): Future[Any] =
    (hp ? CreateHome(homeId, owner, address, houseArea)).mapTo[Any]

  override def deviceStatus(homeId: String, deviceId: String, status: String, optTemp: Option[Int]): Future[Any] =
    (hp ? DeviceStatus(homeId, deviceId, status, optTemp)).mapTo[Any]

  override def addDevice(homeId: String, device: AllDevices): Future[Any] =
    (hp ? AddDevice(homeId, device)).mapTo[Any]

  override def removeDevice(homeId: String, deviceId: String): Future[Any] =
    (hp ? RemoveDevice(homeId, deviceId)).mapTo[Any]

  override def getHome(homeId: String): Future[GetHomeResponse] =
    (hp ? GetHome(homeId)).mapTo[GetHomeResponse]

  override def getDevice(homeId: String, deviceId: String): Future[GetDeviceResponse] =
    (hp ? GetDevice(homeId, deviceId)).mapTo[GetDeviceResponse]

  override def getDeviceStatus(homeId: String, deviceId: String): Future[GetDeviceStatusResponse] =
    (hp ? GetDeviceStatus(homeId, deviceId)).mapTo[GetDeviceStatusResponse]

  override def getAllDevices(homeId: String): Future[GetAllDevicesResponse] =
    (hp ? GetAllDevices(homeId)).mapTo[GetAllDevicesResponse]
}