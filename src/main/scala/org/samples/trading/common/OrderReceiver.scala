package org.samples.trading.common

import org.samples.trading.domain.Orderbook

trait OrderReceiver {
  type ME
  val matchingEngines: List[ME]
  var matchingEnginePartitionsIsStale = true
  var matchingEngineForOrderbook: Map[String, Option[ME]] = Map()

  def refreshMatchingEnginePartitions {
    val m = Map() ++
        (for{
          me <- matchingEngines
          o <- supportedOrderbooks(me)
        } yield (o.symbol, Some(me)))

    matchingEngineForOrderbook = m
    matchingEnginePartitionsIsStale = false
  }

  def supportedOrderbooks(me: ME): List[Orderbook]

}
