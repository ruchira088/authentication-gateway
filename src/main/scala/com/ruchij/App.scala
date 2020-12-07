package com.ruchij

import cats.effect.{Blocker, Clock, ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Resource}
import com.ruchij.config.ServiceConfiguration
import com.ruchij.services.authentication.AuthenticationServiceImpl
import com.ruchij.services.hashing.MurmurHash3Service
import com.ruchij.services.health.HealthServiceImpl
import com.ruchij.services.proxy.ProxyServiceImpl
import com.ruchij.web.Routes
import org.http4s.HttpApp
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder
import pureconfig.ConfigSource

import scala.concurrent.ExecutionContext

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      configObjectSource <- IO.delay(ConfigSource.defaultApplication)
      serviceConfiguration <- ServiceConfiguration.parse[IO](configObjectSource)

      _ <- httpApp[IO](serviceConfiguration, ExecutionContext.global).use { httpApp =>
        BlazeServerBuilder
          .apply[IO](ExecutionContext.global)
          .withHttpApp(httpApp)
          .bindHttp(serviceConfiguration.httpConfiguration.port, serviceConfiguration.httpConfiguration.host)
          .serve
          .compile
          .drain
      }
    } yield ExitCode.Success

  def httpApp[F[_]: ConcurrentEffect: Clock: ContextShift](
    serviceConfiguration: ServiceConfiguration,
    executionContext: ExecutionContext
  ): Resource[F, HttpApp[F]] =
    for {
      client <- BlazeClientBuilder[F](executionContext).resource
      cpuBlocker <- Blocker[F]
      ioBlocker <- Blocker[F]

      healthService = new HealthServiceImpl[F](serviceConfiguration.buildInformation)
      hashingService = new MurmurHash3Service[F]
      proxyService = new ProxyServiceImpl[F](client, serviceConfiguration.proxyConfiguration)
      authenticationService = new AuthenticationServiceImpl[F](
        hashingService,
        serviceConfiguration.authenticationConfiguration,
        cpuBlocker
      )

    } yield Routes(proxyService, authenticationService, healthService, ioBlocker)
}
