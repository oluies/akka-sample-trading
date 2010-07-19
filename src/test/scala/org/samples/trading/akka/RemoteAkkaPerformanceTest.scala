package org.samples.trading.akka

import org.junit._
import Assert._

import se.scalablesolutions.akka.actor._
import se.scalablesolutions.akka.actor.Actor._

@Ignore
class RemoteAkkaPerformanceTest extends AkkaPerformanceTest {
  override def createTradingSystem: TS = new RemoteAkkaTradingSystem

  // need this so that junit will detect this as a test case
  @Test
  override def dummy {}

}


