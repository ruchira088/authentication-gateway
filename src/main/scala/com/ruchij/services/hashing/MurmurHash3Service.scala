package com.ruchij.services.hashing

import cats.effect.Sync
import cats.implicits._

import java.util.Base64
import scala.util.hashing.MurmurHash3

class MurmurHash3Service[F[_]: Sync] extends HashingService[F] {
  override def hash(input: String): F[String] =
    Sync[F].delay {
      Base64.getEncoder.encodeToString {
        MurmurHash3.stringHash(input).toString.getBytes
      }
    }
      .map(_.trim.filter(_.isLetterOrDigit))
}
