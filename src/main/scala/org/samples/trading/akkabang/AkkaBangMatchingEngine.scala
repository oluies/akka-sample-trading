package org.samples.trading.akkabang

import se.scalablesolutions.akka.actor._
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.dispatch.Future
import se.scalablesolutions.akka.dispatch.FutureTimeoutException
import se.scalablesolutions.akka.dispatch.Dispatchers
import se.scalablesolutions.akka.dispatch.MessageDispatcher

import org.samples.trading.akka._
import org.samples.trading.common.MatchingEngine
import org.samples.trading.domain.Order
import org.samples.trading.domain.Orderbook
import org.samples.trading.domain.Rsp
import org.samples.trading.domain.SupportedOrderbooksReq


class AkkaBangMatchingEngine(val meId2: String, val orderbooks2: List[Orderbook], disp2: MessageDispatcher) 
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
