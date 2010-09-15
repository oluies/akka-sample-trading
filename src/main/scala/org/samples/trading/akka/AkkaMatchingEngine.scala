package org.samples.trading.akka

import se.scalablesolutions.akka.actor._
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.dispatch.Future
import se.scalablesolutions.akka.dispatch.FutureTimeoutException
import se.scalablesolutions.akka.dispatch.Dispatchers
import se.scalablesolutions.akka.dispatch.MessageDispatcher

import org.samples.trading.common.MatchingEngine
import org.samples.trading.domain._
import org.samples.trading.domain.SupportedOrderbooksReq


class AkkaMatchingEngine(val meId: String, val orderbooks: List[Orderbook], disp: Option[MessageDispatcher]) extends Actor with MatchingEngine {
  if (disp.isDefined)
    self.dispatcher = disp.get

  var standby: Option[ActorRef] = None

  def receive = {
    case standbyRef: ActorRef => standby = Some(standbyRef)
    case SupportedOrderbooksReq => self.reply(orderbooks)
    case order: Order => handleOrder(order)
    case unknown => println("Received unknown message: " + unknown)
  }

  def handleOrder(order: Order) {
    orderbooksMap(order.orderbookSymbol) match {
      case Some(orderbook) =>
        //				println(meId + " " + order)

        val pendingStandbyReply: Option[Future[_]] = standby match {
          case Some(s) => Some(s !!! order)
          case None => None
        }

        txLog.storeTx(order)
        orderbook.addOrder(order)
        orderbook.matchOrders
        // wait for standby reply
        pendingStandbyReply.foreach(waitForStandby(_))
        self.reply(new Rsp(true))
      case None =>
        println("Orderbook not handled by this MatchingEngine: " + order.orderbookSymbol)
        self.reply(new Rsp(false))
    }
  }

  override
  def postStop {
    txLog.close()
  }

  def waitForStandby(pendingStandbyFuture: Future[_]) {
    try {
      pendingStandbyFuture.await
    } catch {
      case e: FutureTimeoutException => println("### standby timeout: " + e)
    }
  }


}
