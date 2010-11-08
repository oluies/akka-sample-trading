package org.samples.trading.common

import org.samples.trading.domain.Order
trait TxLog {

  def storeTx(order: Order)

  def close()

}
