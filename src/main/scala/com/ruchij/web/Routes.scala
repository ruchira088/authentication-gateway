package com.ruchij.web

import cats.effect.Sync
import cats.implicits._
import com.ruchij.services.authentication.AuthenticationService
import com.ruchij.services.health.HealthService
import com.ruchij.services.proxy.ProxyService
import com.ruchij.web.middleware.{Authenticator, ExceptionHandler, NotFoundHandler}
import com.ruchij.web.routes.{AuthenticationRoutes, HealthRoutes, ProxyRoutes}
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

object Routes {
  def apply[F[_]: Sync](
    proxyService: ProxyService[F],
    authenticationService: AuthenticationService[F],
    healthService: HealthService[F]
  ): HttpApp[F] = {
    implicit val dsl: Http4sDsl[F] = new Http4sDsl[F] {}

    val routes: HttpRoutes[F] =
      Router(
        "/health" -> HealthRoutes[F](healthService),
        "/authenticate" -> AuthenticationRoutes[F](authenticationService)
      ) <+>
        ProxyRoutes(proxyService)
//        Authenticator[F](authenticationService)(ProxyRoutes(proxyService))

    ExceptionHandler {
      NotFoundHandler(routes)
    }
  }
}
