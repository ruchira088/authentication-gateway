package com.ruchij.web.routes

import cats.data.OptionT
import cats.effect.{Clock, IO}
import com.eed3si9n.ruchij.BuildInfo
import com.ruchij.circe.Encoders.dateTimeEncoder
import com.ruchij.config.ProxyConfiguration
import com.ruchij.services.authentication.AuthenticationService
import com.ruchij.services.proxy.ProxyService
import com.ruchij.test.HttpTestApp
import com.ruchij.test.utils.Providers.{contextShift, stubClock}
import com.ruchij.test.matchers._
import io.circe.literal._
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Request, Response, Status, Uri}
import org.joda.time.DateTime
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Properties

class HealthRoutesSpec extends AnyFlatSpec with Matchers with MockFactory {

  "GET /service" should "return a successful response containing service information" in {
    val dateTime = DateTime.now()
    implicit val clock: Clock[IO] = stubClock[IO](dateTime)

    val proxyService: ProxyService[IO] = new ProxyService[IO] {
      override val configuration: ProxyConfiguration = ProxyConfiguration(uri"https://httpbin.org")

      override def route(request: Request[IO]): OptionT[IO, Response[IO]] = OptionT.none
    }

    val authenticationService: AuthenticationService[IO] = mock[AuthenticationService[IO]]

    val application = HttpTestApp[IO](proxyService, authenticationService)

    val request = Request[IO](uri = Uri(path = "/health"))

    val response = application.run(request).unsafeRunSync()

    val expectedJsonResponse =
      json"""{
        "proxyUrl": ${proxyService.configuration.destination.renderString},
        "serviceName": "http-auth",
        "serviceVersion": ${BuildInfo.version},
        "organization": "com.ruchij",
        "scalaVersion": "2.13.4",
        "sbtVersion": "1.4.6",
        "gitBranch" : "test-branch",
        "gitCommit" : "my-commit",
        "javaVersion": ${Properties.javaVersion},
        "gitBranch" : "test-branch",
        "gitCommit" : "my-commit",
        "buildTimestamp" : null,
        "timestamp": $dateTime
      }"""

    response must beJsonContentType
    response must haveJson(expectedJsonResponse)
    response must haveStatus(Status.Ok)
  }
}
