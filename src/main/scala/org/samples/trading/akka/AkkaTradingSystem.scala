package org.samples.trading.akka

import org.samples.trading.common._

import org.samples.trading.domain.Orderbook
import org.samples.trading.domain.StandbyTradeObserver
import se.scalablesolutions.akka.actor.Actor
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.actor.ActorRef
import se.scalablesolutions.akka.actor.ActorRegistry
import se.scalablesolutions.akka.dispatch.Dispatchers
import se.scalablesolutions.akka.dispatch.MessageDispatcher

class AkkaTradingSystem extends TradingSystem {
  type ME = ActorRef
  type OR = ActorRef

  val orDispatcher = Dispatchers.newExecutorBasedEventDrivenDispatcher("or-dispatcher")
  orDispatcher.withNewThreadPoolWithLinkedBlockingQueueWithUnboundedCapacity  
   .setCorePoolSize(1)
   .setMaxPoolSize(1)
   .buildThreadPool
   
  val meDispatcher = Dispatchers.newExecutorBasedEventDrivenDispatcher("me-dispatcher")
  meDispatcher.withNewThreadPoolWithLinkedBlockingQueueWithUnboundedCapacity  
   .setCorePoolSize(16)
   .setMaxPoolSize(16)
   .buildThreadPool
  
  var matchingEngineForOrderbook: Map[String, Option[ActorRef]] = Map()

  override def createMatchingEngines = {

    var i = 0
    val pairs =
      for (orderbooks: List[Orderbook] <- orderbooksGroupedByMatchingEngine)
      yield {
        i = i + 1
        val me = actorOf(new AkkaMatchingEngine("ME" + i, orderbooks, meDispatcher))
        val orderbooksCopy = orderbooks map (o => new Orderbook(o.symbol) with StandbyTradeObserver)
        val standbyOption =
          if (useStandByEngines) {
            val meStandby = actorOf(new AkkaMatchingEngine("ME" + i + "s", orderbooksCopy, meDispatcher))
            Some(meStandby)
          } else {
            None
          }
  
        (me, standbyOption)
      }

    Map() ++ pairs;
  }

  override def createOrderReceivers: List[ActorRef] = {
    val primaryMatchingEngines = matchingEngines.map(pair => pair._1).toList
    (1 to 10).toList map (i => actorOf(new AkkaOrderReceiver(primaryMatchingEngines, orDispatcher)))
  }


  override def start {
    for ((p, s) <- matchingEngines) {
      p.start
      // standby is optional
      s.foreach(_.start)
      s.foreach(p ! _)
    }
    orderReceivers.foreach(_.start)
  }

  override def shutdown {
    orderReceivers.foreach(_.stop)
    for ((p, s) <- matchingEngines) {
      p.stop
      // standby is optional
      s.foreach(_.stop)
    }
  }
}
