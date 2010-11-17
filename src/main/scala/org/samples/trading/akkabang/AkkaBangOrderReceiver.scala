package org.samples.trading.akkabang

import akka.actor._
import akka.dispatch.MessageDispatcher

import org.samples.trading.akka._
import org.samples.trading.domain._

class AkkaBangOrderReceiver(val matchingEngines2: List[ActorRef], disp2: Option[MessageDispatcher])
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
