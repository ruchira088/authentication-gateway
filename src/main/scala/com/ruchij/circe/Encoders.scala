package com.ruchij.circe

import io.circe.Encoder
import org.http4s.Uri
import org.joda.time.DateTime

object Encoders {
  implicit val dateTimeEncoder: Encoder[DateTime] = Encoder.encodeString.contramap[DateTime](_.toString)

  implicit val uriEncoder: Encoder[Uri] = Encoder.encodeString.contramap[Uri](_.renderString)

  implicit def throwableEncoder[A <: Throwable]: Encoder[A] =
    Encoder.encodeString.contramap[A](_.getMessage)
}
