package com.ruchij.test

import cats.effect.{Blocker, Clock, ContextShift, Sync}
import com.ruchij.config.BuildInformation
import com.ruchij.services.authentication.AuthenticationService
import com.ruchij.services.health.HealthServiceImpl
import com.ruchij.services.proxy.ProxyService
import com.ruchij.web.Routes
import org.http4s.HttpApp

import scala.concurrent.ExecutionContext

object HttpTestApp {
  def apply[F[_]: Sync: Clock: ContextShift](
    proxyService: ProxyService[F],
    authenticationService: AuthenticationService[F]
  ): HttpApp[F] = {
    val buildInformation =
      BuildInformation(Some("test-branch"), Some("my-commit"), None)

    val healthService = new HealthServiceImpl[F](buildInformation, proxyService.configuration)

    Routes[F](proxyService, authenticationService, healthService, Blocker.liftExecutionContext(ExecutionContext.global))
  }
}
