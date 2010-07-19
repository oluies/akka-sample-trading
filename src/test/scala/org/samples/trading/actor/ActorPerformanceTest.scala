package org.samples.trading.actor

import org.junit._
import Assert._

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import scala.actors._

import org.samples.trading.domain._
import org.samples.trading.common._

class ActorPerformanceTest extends PerformanceTest {
  type TS = ActorTradingSystem
  type OR = ActorOrderReceiver

  override def createTradingSystem: TS = new ActorTradingSystem

  override def placeOrder(orderReceiver: ActorOrderReceiver, order: Order): Rsp = {
    val r = orderReceiver.asInstanceOf[ActorOrderReceiver] !? order
    val rsp = r match {
      case r: Rsp => r
      case _ => new Rsp(false)
    }
    rsp
  }


  // need this so that junit will detect this as a test case
  @Test
  def dummy {}

  override def runScenario(scenario: String, orders: List[Order], repeat: Int, numberOfClients: Int, delayMs: Int) = {
    val totalNumberOfRequests = orders.size * repeat * numberOfClients
    val latch = new CountDownLatch(numberOfClients)
    val receivers = tradingSystem.orderReceivers.toIndexedSeq
    val clients = (for (i <- 0 until numberOfClients) yield {
      val receiver = receivers(i % receivers.size)
      new Client(receiver, orders, latch, repeat, delayMs)
    }).toList

    val start = System.nanoTime
    clients.foreach(_.start)
    val ok = latch.await(5000 + (2 + delayMs) * totalNumberOfRequests, TimeUnit.MILLISECONDS)
    val durationNs = (System.nanoTime - start)

    assertTrue(ok)
    assertEquals(numberOfClients * (orders.size / 2) * repeat, TotalTradeCounter.counter.get)
    logMeasurement(scenario, numberOfClients, durationNs, repeat, totalNumberOfRequests, averageRspTimeNs(clients), maxRspTimeNs(clients))
  }

  private def averageRspTimeNs(clients: List[Client]) = {
    var totalDuration = 0L
    var totalNumberOfRequests = 0L
    clients.foreach(totalDuration += _.totalRspTimeNs)
    clients.foreach(totalNumberOfRequests += _.numberOfRequests)
    totalDuration / totalNumberOfRequests
  }

  private def maxRspTimeNs(clients: List[Client]) = {
    var max = 0L
    clients.foreach(c => if (c.maxRspTimeNs > max) max = c.maxRspTimeNs)
    max
  }

  class Client(orderReceiver: ActorOrderReceiver, orders: List[Order], latch: CountDownLatch, repeat: Int, delayMs: Int) extends Actor {
    val numberOfRequests = orders.size * repeat
    var totalRspTimeNs = 0L
    var maxRspTimeNs = 0L

    def this(orderReceiver: ActorOrderReceiver, orders: List[Order], latch: CountDownLatch, repeat: Int) {
      this (orderReceiver, orders, latch, repeat, 0)
    }

    def act() {
      (1 to repeat).foreach(i =>
        {
          //					println("Client repeat: " + i)
          for (o <- orders) {
            val t0 = System.nanoTime
            val rsp = placeOrder(orderReceiver, o)
            val duration = System.nanoTime - t0
            totalRspTimeNs += duration
            if (!rsp.status) {
              throw new IllegalStateException("Invalid rsp")
            }
            if (duration > maxRspTimeNs) maxRspTimeNs = duration
            delay(delayMs)
          }
        }
        )
      latch.countDown()
      exit
    }
  }

}


