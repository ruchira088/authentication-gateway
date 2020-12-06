package com.ruchij.web.middleware

import cats.data.{Kleisli, OptionT}
import cats.implicits._
import cats.{ApplicativeError, MonadError}
import com.ruchij.exceptions.AuthenticationException
import com.ruchij.services.authentication.AuthenticationService
import com.ruchij.services.authentication.AuthenticationService.Token
import org.http4s.{HttpRoutes, RequestCookie, Response}

object Authenticator {
  val AuthenticationCookieName = "authentication"

  def apply[F[_]: MonadError[*[_], Throwable]](
    authenticationService: AuthenticationService[F]
  )(httpApp: HttpRoutes[F]): HttpRoutes[F] =
    Kleisli { request =>
      request.cookies
        .collectFirst { case RequestCookie(AuthenticationCookieName, value) => value }
        .fold[OptionT[F, Response[F]]](
          OptionT.liftF {
            ApplicativeError[F, Throwable].raiseError(AuthenticationException("Authentication cookie not found"))
          }
        ) { cookieValue =>
          OptionT {
            authenticationService
              .isAuthenticated(Token(cookieValue))
              .flatMap { success =>
                if (success) httpApp.run(request).value
                else ApplicativeError[F, Throwable].raiseError(AuthenticationException("Invalid authentication cookie"))
              }
          }
        }
    }
}
