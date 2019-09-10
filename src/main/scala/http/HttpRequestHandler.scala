package http

import java.time.{Instant, LocalDateTime}

import http.entities._
import kafka.{KafkaExtension, KafkaManager}
import persist.cassandra.home.Home
import persist.postgres.model.User
import persist.postgres.repos.UserRepo
import util.{AuthenticationHelper, FutureResult, TimeUtils}
import spray.json._
import scala.concurrent.Future

case class SuccessMessage(createdAt: LocalDateTime, message: String = "CREATED")

import util.UtilFunctions._

trait HttpRequestHandler extends AuthenticationHelper with FutureResult[HttpError] {
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


  def createHome(request: HomeDTO): Future[HttpError Either SuccessResponse] = {
    val home = Home(address = request.address, houseArea = request.houseArea, ownerId = request.ownerId)
    (for {
      _ <- fromFuture(homeService.store(partitionKey, home))
      //      _ <- fromFuture(
      //        homeExt.createHome(home.houseArea, home.ownerId,)
      //      )
    } yield SuccessResponse()).value
  }


  def publishStatusToKafka(request: DeviceStatusRequest): Future[HttpError Either DeviceStatusResponse] = {
    (for {
      _ <- fromFuture(kafkaExt.publish(deviceStatusTopic, kafkaKey, request.toJson.toString))
    } yield DeviceStatusResponse()).value
  }

}
