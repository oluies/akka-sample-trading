package org.samples.trading.actor

import org.junit._
import Assert._

import scala.actors._
import scala.actors.Actor._

class ActorPerformanceNoStandByEnginesTest extends ActorPerformanceTest {
  override def createTradingSystem: TS = new ActorTradingSystem {
    override def useStandByEngines: Boolean = false}

  // need this so that junit will detect this as a test case
  @Test
  override def dummy {}


}



