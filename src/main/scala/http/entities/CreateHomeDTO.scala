package http.entities

import java.util.UUID

final case class CreateHomeDTO(
                          address: String,
                          houseArea: Int,
                          ownerId: UUID)

final case class CreateHomeResponseDTO(
                                        homeId: UUID,
                                        ownerId: UUID,
                                        address: String,
                                        houseArea: Int
                                      )