package org.samples.trading;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.samples.trading.actor.ActorPerformanceTest;
import org.samples.trading.basic.BasicPerformanceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
  BasicPerformanceTest.class, 
  ActorPerformanceTest.class
})
public class AllBenchmarkTests {

}
