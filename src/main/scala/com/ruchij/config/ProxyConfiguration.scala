package com.ruchij.config

import cats.implicits._
import com.ruchij.config.ProxyConfiguration.RouteMapping
import pureconfig.ConfigReader
import pureconfig.error.{CannotConvert, FailureReason}

import scala.util.matching.Regex

case class ProxyConfiguration(mappings: List[RouteMapping])

object ProxyConfiguration {
  val Route: Regex = "(\\S+)\\s*->\\s*(\\S+)".r

  case class RouteMapping(in: String, out: String)

  implicit val routeMappingsConfigReader: ConfigReader[List[RouteMapping]] =
    ConfigReader[String].emap { text =>
      text
        .split(",")
        .map(_.trim)
        .toList
        .traverse[Either[FailureReason, *], RouteMapping] {
          case Route(in, out) => Right(RouteMapping(in, out))

          case input =>
            Left(CannotConvert(input, classOf[RouteMapping].getSimpleName, s"Not valid with regex ${Route.pattern}"))
        }
    }
}
