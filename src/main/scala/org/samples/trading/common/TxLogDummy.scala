package org.samples.trading.common

import org.samples.trading.domain.Order
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.BufferedWriter


class TxLogDummy extends TxLog {

  def storeTx(order: Order) {
  }

  def close() {
  }

}
