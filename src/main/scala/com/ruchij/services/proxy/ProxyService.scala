package com.ruchij.services.proxy

import cats.data.OptionT
import com.ruchij.config.ProxyConfiguration
import org.http4s.{Request, Response}

trait ProxyService[F[_]] {
  val configuration: ProxyConfiguration

  def route(request: Request[F]): OptionT[F, Response[F]]
}
