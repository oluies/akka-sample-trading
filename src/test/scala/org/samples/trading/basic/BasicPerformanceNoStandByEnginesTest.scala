package org.samples.trading.basic

import org.junit._
import Assert._

@Ignore
class BasicPerformanceNoStandByEnginesTest extends BasicPerformanceTest {
  override def createTradingSystem: TS = new BasicTradingSystem {
    override def useStandByEngines: Boolean = false}

  // need this so that junit will detect this as a test case
  @Test
  override def dummy {}
}


