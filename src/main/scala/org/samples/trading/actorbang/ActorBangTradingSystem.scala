package org.samples.trading.actorbang

import org.samples.trading.actor._
import org.samples.trading.domain.Orderbook


class ActorBangTradingSystem extends ActorTradingSystem {

  override
  def createMatchingEngine(meId: String, orderbooks: List[Orderbook]) =
    new ActorBangMatchingEngine(meId, orderbooks)

  override
  def createOrderReceiver(matchingEngines: List[ActorMatchingEngine]) =
    new ActorBangOrderReceiver(matchingEngines)

}