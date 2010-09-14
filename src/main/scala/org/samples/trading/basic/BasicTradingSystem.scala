package org.samples.trading.basic

import org.samples.trading.common._
import org.samples.trading.domain.OrderbookFactory
import org.samples.trading.domain.Orderbook
import org.samples.trading.domain.StandbyTradeObserver

class BasicTradingSystem extends TradingSystem {
  type ME = BasicMatchingEngine
  type OR = BasicOrderReceiver

  override def createMatchingEngines = {
    var i = 0
    val pairs =
      for (orderbooks: List[Orderbook] <- orderbooksGroupedByMatchingEngine)
      yield {
        i = i + 1
        val me = new BasicMatchingEngine("ME" + i, orderbooks)
        val orderbooksCopy = orderbooks map (o => OrderbookFactory.createOrderbook(o.symbol, true))
        val standbyOption =
          if (useStandByEngines) {
            val meStandby = new BasicMatchingEngine("ME" + i + "s", orderbooksCopy)
            Some(meStandby)
          } else {
            None
          }
  
        (me, standbyOption)
      }

    Map() ++ pairs;
  }

  override def createOrderReceivers: List[BasicOrderReceiver] = {
    val primaryMatchingEngines = matchingEngines.map(pair => pair._1).toList
    val result = (1 to 10).toList map (i => new BasicOrderReceiver(primaryMatchingEngines))
    result
  }

  override def start {
    for ((p, s) <- matchingEngines) {
      p.standby = s
    }
  }

  override def shutdown {
    for ((p, s) <- matchingEngines) {
      p.exit
      // standby is optional
      s.foreach(_.exit)
    }
  }

}
