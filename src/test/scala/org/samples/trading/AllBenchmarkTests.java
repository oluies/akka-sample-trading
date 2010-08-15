package org.samples.trading;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.samples.trading.actor.ActorPerformanceTest;
import org.samples.trading.akka.AkkaPerformanceTest;
import org.samples.trading.akkabang.AkkaBangPerformanceTest;
import org.samples.trading.akkahawt.AkkaHawtPerformanceTest;
import org.samples.trading.basic.BasicPerformanceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
  BasicPerformanceTest.class, 
  ActorPerformanceTest.class,
  AkkaPerformanceTest.class,
  AkkaBangPerformanceTest.class,
  AkkaHawtPerformanceTest.class
  
})
public class AllBenchmarkTests {

}
