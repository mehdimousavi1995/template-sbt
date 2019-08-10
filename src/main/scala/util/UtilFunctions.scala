package util

import java.time.LocalDateTime

import org.apache.commons.validator.routines.UrlValidator
import org.bouncycastle.util.encoders.Base64

import scala.language.postfixOps
import scala.util.Random

object UtilFunctions {
  def generateToken(): String = {
    val tokenByteArray = new Array[Byte](32)
    Random.nextBytes(tokenByteArray)
    Base64.toBase64String(tokenByteArray)
  }

  def dateTimeKey(time: LocalDateTime): String = {
    val year = time.getYear
    val month = time.getMonth
    val day = time.getDayOfMonth
    val hour = time.getHour
    s"$year.$month.$day.$hour"
  }

   def validateEndpoint(endpoint: String): Boolean = {
    val urlValidator = new UrlValidator(Array("http", "https"))
    endpoint.startsWith("https://") || endpoint.startsWith("http://") && urlValidator.isValid(endpoint)

  }

}
