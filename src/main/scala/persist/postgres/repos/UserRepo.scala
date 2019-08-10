package persist.postgres.repos

import java.time.LocalDateTime

import persist.postgres.ActorPostgresDriver
import persist.postgres.ActorPostgresDriver.api._
import persist.postgres.model.User
import slick.dbio.Effect
import slick.lifted.ProvenShape
import slick.sql.{FixedSqlAction, SqlAction, SqlStreamingAction}

import scala.concurrent.ExecutionContextExecutor

final class UserRepo(tag: Tag) extends Table[User](tag, "users") {

  def username: Rep[String] = column[String]("username", O.PrimaryKey)

  def fullName = column[String]("full_name")

  def password: Rep[String] = column[String]("password")

  def reputation = column[Int]("counter")

  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")


  def * : ProvenShape[User] = (username, fullName, password, reputation, createdAt) <> (User.tupled, User.unapply)

}

object UserRepo {
  val userRepo: TableQuery[UserRepo] = TableQuery[UserRepo]

  def create(user: User): FixedSqlAction[Int, NoStream, Effect.Write] =
    userRepo += user

  def find(userId: Int, username: String): SqlAction[Option[User], NoStream, Effect.Read] =
    userRepo.filter(s => s.username === username).result.headOption

  def authenticate(username: String, hashPassword: String) =
    userRepo.filter(s => s.username === username && s.password === hashPassword).exists.result

  def exists(username: String): FixedSqlAction[Boolean, ActorPostgresDriver.api.NoStream, Effect.Read] =
    userRepo.filter(s ⇒ s.username === username).exists.result

  def notExists(username: String)(implicit ec: ExecutionContextExecutor): DBIOAction[Boolean, NoStream, Effect.Read] =
    exists(username).map(!_)


  def increment(username: String) =
    sql"""update users set reputation = reputation + 1 where username = $username""".as[Int]
}
