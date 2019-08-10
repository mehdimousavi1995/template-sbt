package util

import akka.http.scaladsl.model.RemoteAddress
import akka.http.scaladsl.server.directives.Credentials
import http.HttpServiceRoutes

import scala.concurrent.Future


trait MD5Helper {
  def hash(s: String): String = {
    val m = java.security.MessageDigest.getInstance("MD5")
    val b = s.getBytes("UTF-8")
    m.update(b, 0, b.length)
    new java.math.BigInteger(1, m.digest()).toString(16)
  }
}

trait AuthenticationHelper extends MD5Helper {
  this: HttpServiceRoutes =>

  def authenticator(optToken: Option[String], credentials: Credentials, ip: RemoteAddress): Future[Option[String]] = {
    credentials match {
      case p@Credentials.Provided(identifier) ⇒
        redisExt.get(identifier + optToken.getOrElse("")).map {
          case Some(token) ⇒
            if (p.verify(token)) {
              Some(identifier)
            } else None
          case None ⇒
            None
        }
      case _ ⇒
        log.warning("user authentication failed, caused by: authorization parameter not provided")
        Future.successful(None)
    }
  }

}
