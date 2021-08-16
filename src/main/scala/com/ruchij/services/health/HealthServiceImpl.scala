package com.ruchij.services.health

import cats.effect.{Clock, Sync}
import cats.implicits._
import com.ruchij.config.{BuildInformation, ProxyConfiguration}
import com.ruchij.services.health.models.ServiceInformation
import org.joda.time.DateTime

class HealthServiceImpl[F[_]: Sync](
  buildInformation: BuildInformation,
  proxyConfiguration: ProxyConfiguration
) extends HealthService[F] {
  override def serviceInformation(): F[ServiceInformation] =
    Clock[F].realTimeInstant
      .flatMap(instant => ServiceInformation.create(proxyConfiguration, new DateTime(instant.getEpochSecond), buildInformation))
}
