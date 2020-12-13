package com.ruchij.services.proxy

import cats.data.OptionT
import cats.effect.Sync
import cats.implicits._
import com.ruchij.config.ProxyConfiguration
import org.http4s.client.Client
import org.http4s.headers.Host
import org.http4s.{Request, Response, Uri}

class ProxyServiceImpl[F[_]: Sync](client: Client[F], override val configuration: ProxyConfiguration) extends ProxyService[F] {

  override def route(request: Request[F]): OptionT[F, Response[F]] =
    OptionT.liftF {
      client
        .run {
          Request(
            request.method,
            Uri(
              configuration.destination.scheme,
              configuration.destination.authority,
              configuration.destination.path + request.uri.path,
              request.uri.query,
              request.uri.fragment
            ),
            request.httpVersion,
            request.headers
              .put(Host(configuration.destination.authority.map(_.host.value).getOrElse("localhost"))),
            request.body,
            request.attributes
          )
        }
        .allocated
        .map {
          case (response, finalizer) =>
            response
              .withBodyStream(response.body.onFinalizeWeak(finalizer))
        }
    }

}
