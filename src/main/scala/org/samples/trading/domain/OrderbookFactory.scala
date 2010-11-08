package org.samples.trading.domain

object OrderbookFactory {

  val useDummyOrderbook = System.getProperty("useDummyOrderbook", "false").toBoolean

  def createOrderbook(symbol: String, standby: Boolean) = {
    standby match {
      case false if !useDummyOrderbook => new Orderbook(symbol) with SimpleTradeObserver
      case true if !useDummyOrderbook => new Orderbook(symbol) with StandbyTradeObserver
      case false if useDummyOrderbook => new DummyOrderbook(symbol) with SimpleTradeObserver
      case true if useDummyOrderbook => new DummyOrderbook(symbol) with StandbyTradeObserver
    }

  }
}