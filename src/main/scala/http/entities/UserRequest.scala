package http.entities


final case class UserRequest(
                        fullName: String,
                        username: String,
                        password: String
                      )
