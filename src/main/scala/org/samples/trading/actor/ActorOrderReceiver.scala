package org.samples.trading.actor

import org.samples.trading.common.OrderReceiver
import scala.actors._
import scala.actors.Actor._
import scala.actors.threadpool._

import org.samples.trading.domain.Order
import org.samples.trading.domain.Orderbook
import org.samples.trading.domain.SupportedOrderbooksReq
import org.samples.trading.domain.Rsp

class ActorOrderReceiver(val matchingEngines: List[ActorMatchingEngine], val threadPool: ExecutorService) 
    extends Actor with OrderReceiver {
  type ME = ActorMatchingEngine
  
  override def scheduler = new SchedulerAdapter {
      def execute(block: => Unit) =
        threadPool.execute(new Runnable {
          def run() { block }
        })
    }

  def act() {
    loop {
      react {
        case order: Order => placeOrder(order)
        case "exit" => exit
        case unknown => println("Received unknown message: " + unknown)
      }
    }
  }

  private def placeOrder(order: Order) = {
    if (matchingEnginePartitionsIsStale) refreshMatchingEnginePartitions
    val matchingEngine = matchingEngineForOrderbook(order.orderbookSymbol)
    matchingEngine match {
      case Some(m) =>
        // println("receiver " + order)
        m.forward(order)
      case None =>
        println("Unknown orderbook: " + order.orderbookSymbol)
        reply(new Rsp(false))
    }
  }

  override
  def supportedOrderbooks(me: ActorMatchingEngine): List[Orderbook] = {
    val r = (me !? SupportedOrderbooksReq)
    val rsp = r match {
      case r: List[Orderbook] => r
      case _ => Nil
    }
    rsp
  }
}
