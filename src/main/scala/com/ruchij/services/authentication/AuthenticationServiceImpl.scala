package com.ruchij.services.authentication

import cats.effect.{Blocker, Clock, ContextShift, Sync}
import cats.implicits._
import cats.{Applicative, ApplicativeError}
import com.ruchij.config.AuthenticationConfiguration
import com.ruchij.exceptions.IncorrectPasswordException
import com.ruchij.services.authentication.AuthenticationService.Token
import com.ruchij.services.hashing.HashingService
import org.joda.time.DateTime
import org.mindrot.jbcrypt.BCrypt

import java.util.concurrent.TimeUnit

class AuthenticationServiceImpl[F[_]: Sync: ContextShift: Clock](
  hashingService: HashingService[F],
  authenticationConfiguration: AuthenticationConfiguration,
  blocker: Blocker
) extends AuthenticationService[F] {

  val currentToken: F[Token] =
    Clock[F]
      .realTime(TimeUnit.MILLISECONDS)
      .map(timestamp => new DateTime(timestamp))
      .flatMap { dateTime =>
        blocker.blockOn {
          repeatHash(10, 20, dateTime.toLocalDate.toString + authenticationConfiguration.saltedHash)
        }
      }
      .map { tokenValue =>
        Token(tokenValue)
      }

  def repeatHash(count: Int, maxLength: Int, value: String): F[String] =
    if (count == 0) Applicative[F].pure(value.take(maxLength))
    else
      hashingService.hash(value).flatMap(hashedValue => repeatHash(count - 1, maxLength, hashedValue.take(maxLength)))

  override def authenticate(secret: AuthenticationService.Secret): F[AuthenticationService.Token] =
    blocker
      .delay {
        BCrypt.checkpw(secret.value, authenticationConfiguration.saltedHash)
      }
      .flatMap { isSuccess =>
        if (isSuccess) currentToken
        else ApplicativeError[F, Throwable].raiseError(IncorrectPasswordException)
      }

  override def isAuthenticated(token: AuthenticationService.Token): F[Boolean] =
    currentToken.map(_ == token)
}
