package http

import scala.util.control.NoStackTrace

trait HttpError extends Exception with NoStackTrace {
  val code: Int
  val error: String
}

case object InvalidUrl extends HttpError {
  override val code: Int = 400
  override val error: String = "INVALID_URL"
}

case object InternalServerError extends HttpError {
  override val code: Int = 500
  override val error: String = "INTERNAL_SERVER_ERROR"
}

case object DuplicatePrimaryKey extends HttpError {
  override val code: Int = 500
  override val error: String = "DUPLICATE_KEY"
}

case object WrongUsernameOrPassword extends HttpError {
  override val code: Int = 401
  override val error: String = "WRONG_USERNAME_OR_PASSWORD"
}

case object OwnerNotFound extends HttpError {
  override val code: Int = 404
  override val error: String = "OWNER_NOT_FOUND"
}

case object HomeNotFound extends HttpError {
  override val code: Int = 404
  override val error: String = "HOME_NOT_FOUND"
}
case object DeviceNotFound extends HttpError {
  override val code: Int = 404
  override val error: String = "DEVICE_NOT_FOUND"
}

case object InvalidDeviceStatus extends HttpError {
  override val code: Int = 400
  override val error: String = "INVALID_DEVICE_STATUS, DEVICE_STATUS_SHOULD_BE_ONE_OF: {OFF, ON, HEATER, COOLER}"
}