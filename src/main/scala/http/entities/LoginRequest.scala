package http.entities

final case class LoginRequest(username: String, password: String)
final case class LoginResponse(token: String, message: String)
