package org.samples.trading.domain

abstract case class Order(
    val orderbookSymbol: String,
    val price: Long,
    val volume: Long) {
}

case class Bid(
    orderbookSymbol2: String,
    price2: Long,
    volume2: Long)
    extends Order(orderbookSymbol2, price2, volume2) {
  
  def split(newVolume: Long) = {
    new Bid(orderbookSymbol, price, newVolume)
  }
}


case class Ask(
    orderbookSymbol2: String,
    price2: Long,
    volume2: Long)
    extends Order(orderbookSymbol2, price2, volume2) {
  
  def split(newVolume: Long) = {
    new Ask(orderbookSymbol, price, newVolume)
  }
}
