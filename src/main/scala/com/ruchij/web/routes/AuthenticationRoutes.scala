package com.ruchij.web.routes

import cats.effect.Sync
import cats.implicits._
import com.ruchij.services.authentication.AuthenticationService
import com.ruchij.services.authentication.AuthenticationService.Secret
import com.ruchij.web.middleware.Authenticator
import com.ruchij.web.requests.LoginRequest
import com.ruchij.web.responses.AuthenticationTokenResponse
import org.http4s.{HttpRoutes, ResponseCookie}
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import io.circe.generic.auto._
import org.http4s.dsl.Http4sDsl

object AuthenticationRoutes {
  def apply[F[_]: Sync](authenticationService: AuthenticationService[F])(implicit dsl: Http4sDsl[F]): HttpRoutes[F] = {
    import dsl._

    HttpRoutes.of {
      case GET -> Root => ???

      case request @ POST -> Root =>
        for {
          loginRequest <- request.as[LoginRequest]
          token <- authenticationService.authenticate(Secret(loginRequest.secret))

          response <- Ok(AuthenticationTokenResponse(token.value))
        }
        yield response.addCookie(ResponseCookie(Authenticator.AuthenticationCookieName, token.value))
    }
  }
}
