package http

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

import spray.json.{DeserializationException, JsString, JsValue, JsonFormat}

trait HttpImplicitConversions {
  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(uuid: UUID) = JsString(uuid.toString)
    def read(value: JsValue) = {
      value match {
        case JsString(uuid) if uuid.isEmpty => UUID.randomUUID()
        case JsString(uuid) => UUID.fromString(uuid)
        case _              => throw new DeserializationException("Expected hexadecimal UUID string")
      }
    }
  }

  implicit object LocalDateTimeFormat extends JsonFormat[LocalDateTime] {
    def write(dateTime: LocalDateTime) = JsString(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    def read(value: JsValue) = value match {
      case JsString(dateTime) => LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
      case _ =>  throw new DeserializationException("Field spec expected")
    }
  }

}
