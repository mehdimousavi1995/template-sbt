package persist.cassandra.owner

import java.util.UUID

import util.TimeUtils
import util.TimeUtils._

final case class Owner(
                        ownerId: UUID = UUID.randomUUID(),
                        firstName: String,
                        lastName: String,
                        telegramUserId: Int,
                        createdAt: Long = TimeUtils.nowTehran.toEpochMilliTehran
                      )

