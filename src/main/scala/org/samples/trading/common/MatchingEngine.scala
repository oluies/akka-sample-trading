package org.samples.trading.common

import org.samples.trading.domain.Orderbook

trait MatchingEngine {
  val meId: String
  val orderbooks: List[Orderbook]
  val supportedOrderbookSymbols = orderbooks map (_.symbol)
  protected val orderbooksMap: Map[String, Option[Orderbook]] = 
    Map() ++ (orderbooks map (o => (o.symbol, Some(o))))

  protected val txLog: TxLog = 
    if (useTxLogFile) 
      new TxLogFile(meId + ".txlog")
    else
      new TxLogDummy
  
  def useTxLogFile() = {
      val prop = System.getProperty("useTxLogFile")
      (prop == null) || (prop == "true")  
    }
}
