package org.samples.trading.akkabang

import org.samples.trading.common.OrderReceiver
import se.scalablesolutions.akka.actor._
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.dispatch.Dispatchers
import se.scalablesolutions.akka.dispatch.MessageDispatcher

import org.samples.trading.akka._
import org.samples.trading.domain._

class AkkaBangOrderReceiver(val matchingEngines2: List[ActorRef], disp2: MessageDispatcher)
    extends AkkaOrderReceiver(matchingEngines2, disp2) {

  override def placeOrder(order: Order) = {
    if (matchingEnginePartitionsIsStale) refreshMatchingEnginePartitions
    val matchingEngine = matchingEngineForOrderbook(order.orderbookSymbol)
    matchingEngine match {
      case Some(m) =>
//        				println("receiver " + order)
        m ! order
      case None =>
        println("Unknown orderbook: " + order.orderbookSymbol)
    }
  }
}
