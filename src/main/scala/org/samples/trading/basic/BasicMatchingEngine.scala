package org.samples.trading.basic

import org.samples.trading.common.MatchingEngine
import org.samples.trading.domain.Order
import org.samples.trading.domain.Orderbook
import org.samples.trading.domain.Rsp

class BasicMatchingEngine(val meId: String, val orderbooks: List[Orderbook]) extends MatchingEngine {
  var standby: Option[BasicMatchingEngine] = None

  def matchOrder(order: Order): Rsp = synchronized{
    orderbooksMap(order.orderbookSymbol) match {
      case Some(orderbook) =>
        txLog.storeTx(order)
        orderbook.addOrder(order)
        orderbook.matchOrders

        standby match {
          case None =>
          case Some(standbyMatchingEngine) => standbyMatchingEngine.matchOrder(order)
        }

        new Rsp(true)
      case None =>
        throw new IllegalArgumentException("Orderbook not handled by this MatchingEngine: " + order.orderbookSymbol)
    }
  }

  def supportedOrderbooks = orderbooks;

  def exit() {
    txLog.close()
  }

}
