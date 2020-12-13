package com.ruchij.services.health.models

import cats.effect.Sync
import cats.implicits.toFunctorOps
import com.eed3si9n.ruchij.BuildInfo
import com.ruchij.config.{BuildInformation, ProxyConfiguration}
import org.http4s.Uri
import org.joda.time.DateTime

import scala.util.Properties

case class ServiceInformation(
  proxyUrl: Uri,
  serviceName: String,
  serviceVersion: String,
  organization: String,
  scalaVersion: String,
  sbtVersion: String,
  javaVersion: String,
  gitBranch: Option[String],
  gitCommit: Option[String],
  buildTimestamp: Option[DateTime],
  timestamp: DateTime
)

object ServiceInformation {
  def create[F[_]: Sync](
    proxyConfiguration: ProxyConfiguration,
    timestamp: DateTime,
    buildInformation: BuildInformation
  ): F[ServiceInformation] =
    Sync[F]
      .delay(Properties.javaVersion)
      .map { javaVersion =>
        ServiceInformation(
          proxyConfiguration.destination,
          BuildInfo.name,
          BuildInfo.version,
          BuildInfo.organization,
          BuildInfo.scalaVersion,
          BuildInfo.sbtVersion,
          javaVersion,
          buildInformation.gitBranch,
          buildInformation.gitCommit,
          buildInformation.buildTimestamp,
          timestamp
        )
      }
}
