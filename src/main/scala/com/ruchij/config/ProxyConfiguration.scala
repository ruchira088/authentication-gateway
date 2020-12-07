package com.ruchij.config

import org.http4s.Uri
import pureconfig.ConfigReader
import pureconfig.error.CannotConvert

case class ProxyConfiguration(destination: Uri)

object ProxyConfiguration {
  implicit val uriConfigReader: ConfigReader[Uri] =
    ConfigReader[String].emap { text =>
      Uri.fromString(text).left.map { failure => CannotConvert(text, classOf[Uri].getSimpleName, failure.details) }
    }
}
