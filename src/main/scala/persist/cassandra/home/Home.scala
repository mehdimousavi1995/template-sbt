package persist.cassandra.home

import java.util.UUID

import util.TimeUtils
import util.TimeUtils._

final case class Home(
                       homeId: UUID = UUID.randomUUID(),
                       address: String,
                       houseArea: Int,
                       ownerId: UUID,
                       createdAt: Long = TimeUtils.nowTehran.toEpochMilliTehran
                      )
