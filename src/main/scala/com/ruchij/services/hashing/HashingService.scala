package com.ruchij.services.hashing

trait HashingService[F[_]] {
  def hash(input: String): F[String]
}
