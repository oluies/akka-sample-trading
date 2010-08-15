package org.samples.trading.akkahawt

import org.samples.trading.common._

import org.samples.trading.akkabang._
import org.samples.trading.domain.Orderbook
import org.samples.trading.domain.StandbyTradeObserver
import se.scalablesolutions.akka.actor.Actor
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.actor.ActorRef
import se.scalablesolutions.akka.actor.ActorRegistry
import se.scalablesolutions.akka.dispatch.Dispatchers
import se.scalablesolutions.akka.dispatch.HawtDispatcher
import se.scalablesolutions.akka.dispatch.MessageDispatcher

class AkkaHawtTradingSystem extends AkkaBangTradingSystem {

  lazy val hawtDispatcher = new HawtDispatcher(false)
  
//  override
//  def createOrderReceiverDispatcher: MessageDispatcher = hawtDispatcher
  
  override
  def createMatchingEngineDispatcher: MessageDispatcher = hawtDispatcher
  
  override def start {
    super.start()
    
    for ((p, s) <- matchingEngines) {
//      HawtDispatcher.pin(p)
      if (s != None) {
//        HawtDispatcher.pin(s.get)
        HawtDispatcher.target(p, HawtDispatcher.queue(s.get))
      }
    }
  }
  

}