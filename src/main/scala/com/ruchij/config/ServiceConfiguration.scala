package com.ruchij.config

import cats.ApplicativeError
import com.ruchij.config.BuildInformation
import com.ruchij.config.ConfigReaders.dateTimeConfigReader
import ProxyConfiguration.uriConfigReader
import com.ruchij.types.FunctionKTypes.eitherToF
import pureconfig.ConfigObjectSource
import pureconfig.error.ConfigReaderException
import pureconfig.generic.auto._

case class ServiceConfiguration(
  httpConfiguration: HttpConfiguration,
  authenticationConfiguration: AuthenticationConfiguration,
  proxyConfiguration: ProxyConfiguration,
  buildInformation: BuildInformation
)

object ServiceConfiguration {
  def parse[F[_]: ApplicativeError[*[_], Throwable]](configObjectSource: ConfigObjectSource): F[ServiceConfiguration] =
    eitherToF.apply {
      configObjectSource.load[ServiceConfiguration].left.map(ConfigReaderException.apply)
    }
}
