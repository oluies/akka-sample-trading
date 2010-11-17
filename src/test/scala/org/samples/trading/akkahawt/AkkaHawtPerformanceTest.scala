package org.samples.trading.akkahawt

import org.junit._
import Assert._

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import org.samples.trading.akka._
import org.samples.trading.akkabang._
import org.samples.trading.domain._
import org.samples.trading.common._

import akka.actor.ActorRef
import akka.actor.Actor.actorOf

class AkkaHawtPerformanceTest extends AkkaBangPerformanceTest {

  override def createTradingSystem: TS = new AkkaHawtTradingSystem {
    override
    def createMatchingEngine(meId: String, orderbooks: List[Orderbook]) = 
      actorOf(new AkkaBangMatchingEngine(meId, orderbooks, meDispatcher) with LatchMessageCountDown)
  }

  // need this so that junit will detect this as a test case
  @Test
  override def dummy {}
  
}

