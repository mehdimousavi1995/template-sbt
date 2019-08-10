package http

import java.time.LocalDateTime

import http.entities._
import persist.postgres.model.User
import persist.postgres.repos.UserRepo
import util.{AuthenticationHelper, FutureResult, TimeUtils}

import scala.concurrent.Future

case class SuccessMessage(createdAt: LocalDateTime, message: String = "CREATED")

import util.UtilFunctions._

trait HttpRequestHandler extends AuthenticationHelper with FutureResult[HttpError] {
  this: HttpServiceRoutes =>

  def createUser(user: UserRequest): Future[HttpError Either SuccessMessage] = {
    val createdAt = TimeUtils.nowTehran
    (for {
      _ <- fromFuture(_ => DuplicatePrimaryKey)(pdb.run(UserRepo.create(User(user.fullName, user.username, hash(user.password), createdAt))))
    } yield SuccessMessage(createdAt)).value
  }

  val expirationTime = 86400 // 1 day

  def login(request: LoginRequest): Future[HttpError Either LoginResponse] = {
    (for {
      _ <- fromFutureBoolean(WrongUsernameOrPassword)(pdb.run(UserRepo.authenticate(request.username, request.password)))
      token = generateToken()
      redisKey = request.username + token
      _ <- fromFuture(redisExt.psetex(redisKey, expirationTime, token))
    } yield LoginResponse(token, "AUTHENTICATED")).value
  }



}
