package com.ruchij.test.utils

import cats.effect.IO
import cats.effect.unsafe.implicits.global

object IOUtils {

  def runIO[A](thunk: IO[A]): A = thunk.unsafeRunSync()

}
