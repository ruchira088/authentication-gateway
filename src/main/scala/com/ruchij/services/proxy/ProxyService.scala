package com.ruchij.services.proxy

import cats.data.OptionT
import org.http4s.{Request, Response}

trait ProxyService[F[_]] {
  def route(request: Request[F]): OptionT[F, Response[F]]
}
