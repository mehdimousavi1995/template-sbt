package botaccess

import akka.http.scaladsl.util.FastFuture

import scala.concurrent.Future

private trait BotAccessQueryHandler {
  this: BotAccessProcessor ⇒

  def getRoles: Future[Seq[String]] =
    FastFuture.successful(state.roles)

  def hasAccess(service: String, api: String): Future[Boolean] =
    Future.successful(true)

  def hasPremiumAccess(service: String, api: String): Future[Boolean] = {
    Future.successful(true)
  }

  def getPremiumAccess: Future[Seq[String]] = Future.successful(state.premiumAccesses)

}