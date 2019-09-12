package http

import java.time.LocalDateTime

import http.entities._
import kafka.{KafkaExtension, KafkaManager}
import main.Constant._
import messages.homeee.homessages._
import persist.cassandra.device.Device
import persist.cassandra.home.Home
import persist.cassandra.owner.Owner
import persist.postgres.model.User
import persist.postgres.repos.UserRepo
import spray.json._
import util.{AuthenticationHelper, FutureResult, ImplicitConversions, TimeUtils}

import scala.concurrent.Future

case class SuccessMessage(createdAt: LocalDateTime, message: String = "CREATED")

import util.UtilFunctions._

trait HttpRequestHandler extends AuthenticationHelper
  with FutureResult[HttpError]
  with ImplicitConversions {
  this: HttpServiceRoutes =>

  val kafkaExt: KafkaManager = KafkaExtension(system).broker
  val deviceStatusTopic = "device-status-topic"
  val kafkaKey = "kafkaKey"

  def createUser(user: UserRequest): Future[HttpError Either SuccessMessage] = {
    val createdAt = TimeUtils.nowTehran
    (for {
      _ <- fromFuture(_ => DuplicatePrimaryKey)(pdb.run(UserRepo.create(User(user.username, user.fullName, hash(user.password), createdAt))))
    } yield SuccessMessage(createdAt)).value
  }

  val expirationTime = 86400 // 1 day

  def login(request: LoginRequest): Future[HttpError Either LoginResponse] = {
    (for {
      _ <- fromFutureBoolean(WrongUsernameOrPassword)(pdb.run(UserRepo.authenticate(request.username, hash(request.password))))
      token = generateToken()
      _ <- fromFuture(redisExt.psetex(request.username, expirationTime, token))
    } yield LoginResponse(token, "AUTHENTICATED")).value
  }


  def createHome(request: CreateHomeDTO): Future[HttpError Either CreateHomeResponseDTO] = {
    val home = Home(address = request.address, houseArea = request.houseArea, ownerId = request.ownerId)
    (for {
      owner <- fromFutureOption(OwnerNotFound)(ownerService.findById(partitionKey, request.ownerId))
      _ <- fromFuture(homeService.store(partitionKey, home))
      _ <- fromFuture(homeExt.createHome(home.homeId.toString, owner, home.address, home.houseArea, home.createdAt))
    } yield CreateHomeResponseDTO(home.homeId, home.ownerId, home.address, home.houseArea)).value
  }

  def createOwner(request: CreateOwnerDTO): Future[HttpError Either CreateOwnerResponseDTO] = {
    val owner = Owner(
      firstName = request.firstName,
      lastName = request.lastName,
      telegramUserId = request.telegramUserId
    )
    (
      for {
        _ <- fromFuture(ownerService.store(partitionKey, owner))
      } yield CreateOwnerResponseDTO(owner.ownerId, owner.firstName, owner.lastName, owner.telegramUserId)).value
  }

  implicit class FutureOptionList[T](l: Future[List[Option[T]]]) {
    def filterOption(): Future[List[T]] = {
      l.map(_.filter(_.isDefined).map(_.get))
    }
  }


  def createDeviceWith(request: Device): AllDevices = {
    request.deviceType match {
      case LAMP =>
        val lamp = LampDevice(request.deviceId.toString, request.deviceName, OnOrOffStatus.OFF)
        AllDevices().withLampDevice(lamp)
      case HEATER_COOLER =>
        val heaterCooler = HeaterCooler(request.deviceId.toString, request.deviceName, HeaterCoolerState.OFFLINE, 0)
        AllDevices().withHeaterCooler(heaterCooler)
    }
  }

  def createDevices(request: DeviceDTO): Future[HttpError Either DeviceResponseDTO] = {
    (for {
      _ <- fromFutureOption(HomeNotFound)(homeService.findById(partitionKey, request.homeId))
      device = Device(deviceName = request.deviceName, deviceType = request.deviceType, homeId = request.homeId)
      _ <- fromFuture(deviceService.store(partitionKey, device))
      allDevice = createDeviceWith(device)
      _ <- fromFuture(homeExt.addDevice(device.homeId.toString, allDevice))
    } yield DeviceResponseDTO(device.deviceId, device.homeId, device.deviceName, device.deviceType)).value
  }

  def publishStatusToKafka(request: DeviceStatusRequest): Future[HttpError Either DeviceStatusResponse] = {
    (for {
      _ <- fromFutureOption(DeviceNotFound)(deviceService.findById(partitionKey, request.deviceId))
      _ <- fromFutureOption(HomeNotFound)(homeService.findById(partitionKey, request.homeId))
      _ <- fromBoolean(InvalidDeviceStatus)(List(OFF, ON, HEATER, COOLER).contains(request.status))
      _ <- fromFuture(kafkaExt.publish(deviceStatusTopic, kafkaKey, request.toJson.toString))
    } yield DeviceStatusResponse()).value
  }

  def getStatus(homeId: String, deviceId: String): Future[HttpError Either GetDeviceStatusResponse] = {
    (for {
      device <- fromFuture(homeExt.getDeviceStatus(homeId, deviceId))
    } yield GetDeviceStatusResponse(device.status, device.optTemp)).value
  }

}
