package http.entities

import java.util.UUID

final case class CreateOwnerDTO(
                        firstName: String,
                        lastName: String,
                        telegramUserId: Int
                      )

final case class CreateOwnerResponseDTO(
                                       ownerId: UUID,
                                       firstName: String,
                                       lastName: String,
                                       telegramUserId: Int
                                       )