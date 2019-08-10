package persist.postgres.model

import java.time.LocalDateTime

import util.TimeUtils


@SerialVersionUID(1L)
final case class User(
                       username: String,
                       fullName: String,
                       password: String,
                       reputation: Int,
                       createdAt: LocalDateTime = TimeUtils.nowTehran
               )