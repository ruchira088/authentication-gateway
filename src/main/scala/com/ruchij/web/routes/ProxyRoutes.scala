package com.ruchij.web.routes

import cats.effect.Sync
import com.ruchij.services.proxy.ProxyService
import org.http4s.HttpRoutes

object ProxyRoutes {
  def apply[F[_]: Sync](proxyService: ProxyService[F]): HttpRoutes[F] =
    HttpRoutes {
      request => proxyService.route(request)
    }
}
