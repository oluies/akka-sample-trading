package org.samples.trading.akka

import org.samples.trading.common.OrderReceiver
import se.scalablesolutions.akka.actor._
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.dispatch.Dispatchers
import se.scalablesolutions.akka.dispatch.MessageDispatcher

import org.samples.trading.domain._

class AkkaOrderReceiver(val matchingEngines: List[ActorRef], disp: MessageDispatcher)
    extends Actor with OrderReceiver {
  type ME = ActorRef

  self.dispatcher = disp

  def receive = {
    case order: Order => placeOrder(order)
    case unknown => println("Received unknown message: " + unknown)
  }

  override
  def supportedOrderbooks(me: ActorRef): List[Orderbook] = {
    val rsp = (me !! SupportedOrderbooksReq)
    rsp.getOrElse(Nil)
  }


  private def placeOrder(order: Order) = {
    if (matchingEnginePartitionsIsStale) refreshMatchingEnginePartitions
    val matchingEngine = matchingEngineForOrderbook(order.orderbookSymbol)
    matchingEngine match {
      case Some(m) =>
        //				println("receiver " + order)
        m.forward(order)
      case None =>
        println("Unknown orderbook: " + order.orderbookSymbol)
        reply(new Rsp(false))
    }
  }
}
