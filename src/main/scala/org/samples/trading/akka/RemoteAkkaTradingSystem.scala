package org.samples.trading.akka

import se.scalablesolutions.akka.remote.RemoteNode
import se.scalablesolutions.akka.remote.RemoteServer

class RemoteAkkaTradingSystem extends AkkaTradingSystem {
  var server1: RemoteServer = null
  var server2: RemoteServer = null

  override def start() {
    server1 = new RemoteServer
    server1.start("localhost", 9998)
    server2 = new RemoteServer
    server2.start("localhost", 9999)

    for ((p, s) <- matchingEngines) {
      p.makeRemote("localhost", 9999)
      // standby is optional
      s.foreach(_.makeRemote("localhost", 9998))
    }
    orderReceivers.foreach(_.makeRemote("localhost", 9999))

    super.start
  }

  override def shutdown() {
    super.shutdown
    if (server2 != null) {
      server2.shutdown
    }
    if (server1 != null) {
      server1.shutdown
    }

  }


}
