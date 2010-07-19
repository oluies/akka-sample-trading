package org.samples.trading.domain

trait StandbyTradeObserver extends TradeObserver {
  override def trade(bid: Bid, ask: Ask) {
  }
}


