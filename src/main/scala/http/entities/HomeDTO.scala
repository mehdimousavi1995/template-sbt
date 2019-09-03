package http.entities

import java.util.UUID

final case class HomeDTO(
                          address: String,
                          houseArea: Int,
                          ownerId: UUID)
