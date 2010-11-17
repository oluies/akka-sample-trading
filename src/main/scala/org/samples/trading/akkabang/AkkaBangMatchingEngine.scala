package org.samples.trading.akkabang

import akka.actor._
import akka.dispatch.MessageDispatcher

import org.samples.trading.akka._
import org.samples.trading.domain.Order
import org.samples.trading.domain.Orderbook

class AkkaBangMatchingEngine(val meId2: String, val orderbooks2: List[Orderbook], disp2: Option[MessageDispatcher])
  extends AkkaMatchingEngine(meId2, orderbooks2, disp2) {

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
