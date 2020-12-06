package com.ruchij.services.proxy

import cats.data.OptionT
import cats.effect.Sync
import cats.implicits._
import com.ruchij.config.ProxyConfiguration
import com.ruchij.config.ProxyConfiguration.RouteMapping
import org.http4s.Uri.RegName
import org.http4s.client.Client
import org.http4s.{Request, Response}

class ProxyServiceImpl[F[_]: Sync](client: Client[F], proxyConfiguration: ProxyConfiguration)
    extends ProxyService[F] {

  override def route(request: Request[F]): OptionT[F, Response[F]] =
    request.uri.host
      .flatMap { host =>
        proxyConfiguration.mappings
          .find { case RouteMapping(in, _) => in == host.value }
      }
      .fold[OptionT[F, Response[F]]](OptionT.none[F, Response[F]]) {
        case RouteMapping(_, out) =>
          OptionT.liftF {
            client
              .run {
                request.withUri(request.uri.copy(authority = request.uri.authority.map(_.copy(host = RegName(out)))))
              }
              .allocated
              .map { case (response, finalizer) =>
                response.withBodyStream(response.body.onFinalize(finalizer))
              }
          }
      }

}
