package homeee

import akka.actor.{ActorRef, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import messages.homeee.homessages.HomeQuries.{GetAllDevicesResponse, GetDeviceResponse, GetDeviceStatusResponse, GetHomeResponse}
import messages.homeee.homessages.{AllDevices, Owner}

import scala.concurrent.Future


trait HomeExtension extends Extension {

  val hp: ActorRef

  def createHome(homeId: String, owner: Owner, address: String, houseArea: Int, createdAt: Long): Future[Any]

  def addDevice(homeId: String, device: AllDevices): Future[Any]

  def removeDevice(homeId: String, deviceId: String): Future[Any]

  def deviceStatus(homeId: String, deviceId: String, status: String, optTemp: Option[Int]): Future[Any]


  def getHome(homeId: String): Future[GetHomeResponse]

  def getDevice(homeId: String, deviceId: String): Future[GetDeviceResponse]

  def getDeviceStatus(homeId: String, deviceId: String): Future[GetDeviceStatusResponse]

  def getAllDevices(homeId: String): Future[GetAllDevicesResponse]

  def getRunningOnHost(homeId: String): Future[String]
}

object HomeExtension extends ExtensionId[HomeExtension] with ExtensionIdProvider {
  override def lookup = HomeExtension

  override def createExtension(system: ExtendedActorSystem) = new HomeExtensionImp(system)
}