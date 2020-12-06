package com.ruchij.services.authentication

import com.ruchij.services.authentication.AuthenticationService.{Secret, Token}

trait AuthenticationService[F[_]] {
  def authenticate(secret: Secret): F[Token]

  def isAuthenticated(token: Token): F[Boolean]
}

object AuthenticationService {
  case class Secret(value: String) extends AnyVal

  case class Token(value: String) extends AnyVal
}
