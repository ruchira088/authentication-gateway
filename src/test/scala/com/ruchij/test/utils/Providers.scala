package com.ruchij.test.utils

import cats.effect.{Clock, ContextShift, IO, Sync}
import org.joda.time.DateTime

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.TimeUnit
import scala.language.implicitConversions

object Providers {
  implicit def clock[F[_]: Sync]: Clock[F] = stubClock(DateTime.now())

  implicit def stubClock[F[_]: Sync](dateTime: => DateTime): Clock[F] = new Clock[F] {
    override def realTime(unit: TimeUnit): F[Long] =
      Sync[F].delay(unit.convert(dateTime.getMillis, TimeUnit.MILLISECONDS))

    override def monotonic(unit: TimeUnit): F[Long] = realTime(unit)
  }

  implicit def contextShift(implicit executionContext: ExecutionContext): ContextShift[IO] =
    IO.contextShift(executionContext)
}
