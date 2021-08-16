package com.ruchij.web.routes

import cats.effect.IO
import com.ruchij.services.health.models.ServiceInformation
import com.ruchij.test.matchers._
import com.ruchij.test.mixins.io.MockedRoutesIO
import com.ruchij.test.utils.IOUtils.runIO
import io.circe.literal._
import org.http4s.Method.GET
import org.http4s.Status
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class HealthRoutesSpec extends AnyFlatSpec with MockedRoutesIO with Matchers {

  "GET /service" should "return a successful response containing service information" in runIO {

    val expectedJsonResponse =
      json"""{
        "proxyUrl": "https://httpbin.org",
        "serviceName": "http-auth",
        "serviceVersion": "1.0.0",
        "organization": "com.ruchij",
        "scalaVersion": "2.13.6",
        "sbtVersion": "1.5.5",
        "gitBranch" : "test-branch",
        "gitCommit" : "my-commit",
        "javaVersion": "11.0.12",
        "gitBranch" : "test-branch",
        "gitCommit" : "my-commit",
        "buildTimestamp" : null,
        "timestamp": "2021-08-16T22:24:40.000Z"
      }"""

    for {
      _ <- IO.delay {
        (() => healthService.serviceInformation()).expects().returns {
          IO.pure {
            ServiceInformation(
              uri"https://httpbin.org",
              "http-auth",
              "1.0.0",
              "com.ruchij",
              "2.13.6",
              "1.5.5",
              "11.0.12",
              Some("test-branch"),
              Some("my-commit"),
              None,
              new DateTime(2021, 8, 16, 22, 24, 40, 0).withZoneRetainFields(DateTimeZone.UTC)
            )
          }
        }
      }

      routes <- createRoutes()

      response <- routes.run(GET(uri"/health"))

      _ <- IO.delay {
        response must beJsonContentType
        response must haveJson(expectedJsonResponse)
        response must haveStatus(Status.Ok)
      }
    }
    yield (): Unit
  }
}
