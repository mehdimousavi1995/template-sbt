package homeee

import scala.util.control.NoStackTrace

abstract class HomeErrors(val message: String) extends RuntimeException(message) with NoStackTrace

object HomeErrors {

  final case class ThereIsNoPredefinedRole(msg: String) extends HomeErrors(msg)
}
