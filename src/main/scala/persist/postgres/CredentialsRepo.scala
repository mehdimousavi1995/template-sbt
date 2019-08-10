package persist.postgres


import slick.dbio.{Effect, NoStream}
import slick.sql.SqlAction

trait CredentialRecord {
  val username: String
  val password: String
}

trait CredentialsRepo {
  def find(username: String): SqlAction[Option[CredentialRecord], NoStream, Effect.Read]
}
