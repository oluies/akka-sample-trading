package org.samples.trading.actorbang

import org.samples.trading.actor._
import org.samples.trading.domain.Order
import org.samples.trading.domain.Orderbook
class ActorBangMatchingEngine(val meId2: String, val orderbooks2: List[Orderbook])
  extends ActorMatchingEngine(meId2, orderbooks2) {

  override
  def handleOrder(order: Order) {
    orderbooksMap(order.orderbookSymbol) match {
      case Some(orderbook) =>
      //                println(meId + " " + order)

        standby.foreach(_ ! order)

        txLog.storeTx(order)
        orderbook.addOrder(order)
        orderbook.matchOrders

      case None =>
        println("Orderbook not handled by this MatchingEngine: " + order.orderbookSymbol)
    }
  }


}
