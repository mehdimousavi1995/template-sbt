package botaccess

import scala.util.control.NoStackTrace

abstract class BotAccessError(val message: String) extends RuntimeException(message) with NoStackTrace

object BotAccessErrors {

  final case class ThereIsNoPredefinedRole(msg: String) extends BotAccessError(msg)
}
