package com.ruchij.services.health

import cats.effect.{Clock, Sync}
import cats.implicits._
import com.ruchij.config.{BuildInformation, ProxyConfiguration}
import com.ruchij.services.health.models.ServiceInformation
import org.joda.time.DateTime

import java.util.concurrent.TimeUnit

class HealthServiceImpl[F[_]: Clock: Sync](
  buildInformation: BuildInformation,
  proxyConfiguration: ProxyConfiguration
) extends HealthService[F] {
  override def serviceInformation(): F[ServiceInformation] =
    Clock[F]
      .realTime(TimeUnit.MILLISECONDS)
      .flatMap(timestamp => ServiceInformation.create(proxyConfiguration, new DateTime(timestamp), buildInformation))
}
