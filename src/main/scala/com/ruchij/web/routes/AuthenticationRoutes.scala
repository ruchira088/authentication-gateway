package com.ruchij.web.routes

import cats.data.OptionT
import cats.effect.{Blocker, ContextShift, Sync}
import cats.implicits._
import com.ruchij.services.authentication.AuthenticationService
import com.ruchij.services.authentication.AuthenticationService.Secret
import com.ruchij.web.middleware.Authenticator
import com.ruchij.web.requests.LoginRequest
import com.ruchij.web.responses.AuthenticationTokenResponse
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, ResponseCookie, StaticFile}

object AuthenticationRoutes {
  def apply[F[_]: Sync: ContextShift](authenticationService: AuthenticationService[F], ioBlocker: Blocker)(
    implicit dsl: Http4sDsl[F]
  ): HttpRoutes[F] = {
    import dsl._

    HttpRoutes {
      case request @ GET -> Root =>
        StaticFile.fromResource("html/login-page.html", ioBlocker, Some(request))

      case request @ GET -> Root / "background.jpg" =>
        StaticFile.fromResource("images/background.jpg", ioBlocker, Some(request))

      case request @ POST -> Root =>
        OptionT.liftF {
          for {
            loginRequest <- request.as[LoginRequest]
            token <- authenticationService.authenticate(Secret(loginRequest.secret))

            response <- Ok(AuthenticationTokenResponse(token.value))
          } yield response.addCookie(ResponseCookie(Authenticator.AuthenticationCookieName, token.value))
        }

      case _ => OptionT.none
    }
  }
}
