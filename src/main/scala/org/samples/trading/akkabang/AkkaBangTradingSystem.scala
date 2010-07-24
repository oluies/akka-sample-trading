package org.samples.trading.akkabang

import org.samples.trading.common._

import org.samples.trading.akka._
import org.samples.trading.domain.Orderbook
import org.samples.trading.domain.StandbyTradeObserver
import se.scalablesolutions.akka.actor.Actor
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.actor.ActorRef
import se.scalablesolutions.akka.actor.ActorRegistry
import se.scalablesolutions.akka.dispatch.Dispatchers
import se.scalablesolutions.akka.dispatch.MessageDispatcher

class AkkaBangTradingSystem extends AkkaTradingSystem {

  override
  def createMatchingEngine(meId: String, orderbooks: List[Orderbook]) = 
    actorOf(new AkkaBangMatchingEngine(meId, orderbooks, meDispatcher))
    
  override
  def createOrderReceiver(matchingEngines: List[ActorRef]) = 
    actorOf(new AkkaBangOrderReceiver(matchingEngines, orDispatcher))

}