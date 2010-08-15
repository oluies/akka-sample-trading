package org.samples.trading.actorbang

import org.junit._
import Assert._

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import org.samples.trading.actor._
import org.samples.trading.domain._
import org.samples.trading.common._

class ActorBangPerformanceTest extends ActorPerformanceTest {

  override def createTradingSystem: TS = new ActorBangTradingSystem {
    override
    def createMatchingEngine(meId: String, orderbooks: List[Orderbook]) = 
      new ActorBangMatchingEngine(meId, orderbooks, meThreadPool) with LatchMessageCountDown
  }

  override def placeOrder(orderReceiver: ActorOrderReceiver, order: Order): Rsp = {
    val newOrder = LatchOrder(order)
    val r = orderReceiver.asInstanceOf[ActorOrderReceiver] ! newOrder
    val ok = newOrder.latch.await(5, TimeUnit.SECONDS)
    new Rsp(ok)
  }
  
  // need this so that junit will detect this as a test case
  @Test
  override def dummy {}
  

}

trait LatchMessageCountDown extends ActorBangMatchingEngine {
  
  override
  def handleOrder(order: Order) {
    super.handleOrder(order)
    order.asInstanceOf[LatchMessage].latch.countDown
  }
}

