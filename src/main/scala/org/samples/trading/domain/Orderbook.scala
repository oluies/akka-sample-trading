package org.samples.trading.domain

abstract class Orderbook(val symbol: String) {
  var bidSide: List[Bid] = Nil
  var askSide: List[Ask] = Nil

  def addOrder(order: Order) {
    assert(symbol == order.orderbookSymbol)
    order match {
      case bid: Bid =>
        bidSide = (bid :: bidSide).sort(_.price > _.price)
      case ask: Ask =>
        askSide = (ask :: askSide).sort(_.price < _.price)
    }
  }

  def matchOrders() {
    if (!bidSide.isEmpty && !askSide.isEmpty) {
      val topOfBook = (bidSide.head, askSide.head)
      topOfBook match {
        case (bid, ask) if bid.price < ask.price => // no match
        case (bid, ask) if bid.price >= ask.price && bid.volume == ask.volume =>
          trade(bid, ask)
          bidSide = bidSide.tail
          askSide = askSide.tail
          matchOrders
        case (bid, ask) if bid.price >= ask.price && bid.volume < ask.volume =>
          val matchingAsk = ask.split(bid.volume)
          val remainingAsk = ask.split(ask.volume - bid.volume)
          trade(bid, matchingAsk)
          bidSide = bidSide.tail
          askSide = remainingAsk :: askSide.tail
          matchOrders
        case (bid, ask) if bid.price >= ask.price && bid.volume > ask.volume =>
          val matchingBid = bid.split(ask.volume)
          val remainingBid = bid.split(bid.volume - ask.volume)
          trade(matchingBid, ask)
          bidSide = remainingBid :: bidSide.tail
          askSide = askSide.tail
          matchOrders
      }
    }
  }

  def trade(bid: Bid, ask: Ask)


}
