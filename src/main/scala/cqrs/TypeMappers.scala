package cqrs

import java.time.Instant

object TypeMappers {
  def get[E, A](xor: Either[E, A]): A = xor match {
    case Right(res) ⇒ res
    case Left(e)    ⇒ throw new Exception(s"Parse error: ${e}")
  }

  private def applyInstant(millis: Long): Instant = Instant.ofEpochMilli(millis)

  private def unapplyInstant(dt: Instant): Long = dt.toEpochMilli

  implicit val instantMapper: scalapb.TypeMapper[Long, Instant] = scalapb.TypeMapper(applyInstant)(unapplyInstant)

}
