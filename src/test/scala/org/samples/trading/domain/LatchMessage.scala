package org.samples.trading.domain

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

trait LatchMessage {
  val count: Int
  lazy val latch: CountDownLatch = new CountDownLatch(count)
}