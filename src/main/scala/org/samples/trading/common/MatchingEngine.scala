package org.samples.trading.common

import org.samples.trading.domain.Orderbook

trait MatchingEngine {
  val meId: String
  val orderbooks: List[Orderbook]
  val supportedOrderbookSymbols = orderbooks map (_.symbol)
  protected val txLog = new TxLog(meId + ".txlog")
  protected val orderbooksMap: Map[String, Option[Orderbook]] = 
    Map() ++ (orderbooks map (o => (o.symbol, Some(o))))
}
