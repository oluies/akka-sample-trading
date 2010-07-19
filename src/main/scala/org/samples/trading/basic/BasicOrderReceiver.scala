package org.samples.trading.basic

import org.samples.trading.common.OrderReceiver
import org.samples.trading.domain.Order
import org.samples.trading.domain.Rsp
import org.samples.trading.domain.Orderbook

class BasicOrderReceiver(val matchingEngines: List[BasicMatchingEngine]) extends OrderReceiver {
  type ME = BasicMatchingEngine

  def placeOrder(order: Order): Rsp = {
    if (matchingEnginePartitionsIsStale) refreshMatchingEnginePartitions
    matchingEngineForOrderbook(order.orderbookSymbol) match {
      case Some(matchingEngine) =>
        matchingEngine.matchOrder(order)
      case None =>
        throw new IllegalArgumentException("Unknown orderbook: " + order.orderbookSymbol)
    }
  }

  override
  def supportedOrderbooks(me: BasicMatchingEngine): List[Orderbook] = {
    me.supportedOrderbooks
  }


}
