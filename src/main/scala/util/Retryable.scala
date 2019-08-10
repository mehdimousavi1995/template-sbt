package util

import akka.actor.ActorSystem
import akka.pattern.after
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.Random
import DelayCalculator._

object Retryable {

  private val minBackoff = 1.seconds
  private val maxBackoff = 5.seconds

  def retry[T](
    task:             ⇒ Future[T],
    condition:        T ⇒ Boolean = { _: T ⇒ true },
    finiteRetryTimes: Int         = 5,
    retryUntil:       Int         = 0)(implicit system: ActorSystem, ec: ExecutionContextExecutor, timeout: Timeout): Future[T] = {
    if (finiteRetryTimes > 0) {
      task.filter(condition).recoverWith {
        case e ⇒
          system.log.warning(e.getMessage, e)
          after(calculateDelay(retryUntil + 1, minBackoff, maxBackoff, Random.nextDouble()), system.scheduler)(retry(task, condition, finiteRetryTimes - 1, retryUntil + 1)(system, ec, timeout))
      }
    } else task
  }

}
