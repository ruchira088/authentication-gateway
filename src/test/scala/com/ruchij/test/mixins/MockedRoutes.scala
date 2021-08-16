package com.ruchij.test.mixins

import cats.effect.kernel.Async
import com.ruchij.services.authentication.AuthenticationService
import com.ruchij.services.health.HealthService
import com.ruchij.services.proxy.ProxyService
import com.ruchij.web.Routes
import org.http4s.HttpApp
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest

trait MockedRoutes[F[_]] extends MockFactory with OneInstancePerTest {

  val async: Async[F]

  val proxyService: ProxyService[F] = mock[ProxyService[F]]
  val authenticationService: AuthenticationService[F] = mock[AuthenticationService[F]]
  val healthService: HealthService[F] = mock[HealthService[F]]

  def createRoutes(): F[HttpApp[F]] =
    async.delay {
      Routes[F](proxyService, authenticationService, healthService)(async)
    }

}
