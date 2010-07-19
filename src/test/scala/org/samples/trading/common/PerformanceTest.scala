package org.samples.trading.common

import java.util.Random
import org.junit._
import Assert._

import org.samples.trading.domain._

trait PerformanceTest {

  //	jvm parameters
  //	-server -Xms256M -Xmx512M -XX:MaxGCPauseMillis=10

  // Use longRun = 30 for benchmark
  val longRun = 1


  type TS <: TradingSystem
  // TODO is it possible to define tyep OR as type of TradingSystem?
  //type OR = TS#OR
  type OR

  var tradingSystem: TS = _
  val random: Random = new Random()

  def createTradingSystem: TS

  def placeOrder(orderReceiver: OR, order: Order): Rsp

  def runScenario(scenario: String, orders: List[Order], repeat: Int, numberOfClients: Int, delayMs: Int)

  @Before
  def setUp {
    tradingSystem = createTradingSystem
    tradingSystem.start
    warmUp
    TotalTradeCounter.reset
  }

  @After
  def tearDown {
    tradingSystem.shutdown
  }

  private def warmUp {
    val bid = new Bid("A1", 100, 1000)
    val ask = new Ask("A1", 100, 1000)

    val orderReceiver = tradingSystem.orderReceivers.head
    for (i <- 1 to 10) {
      // TODO would like to get rid of those asInstanceOf, see comment in type definition above
      placeOrder(orderReceiver.asInstanceOf[OR], bid)
      placeOrder(orderReceiver.asInstanceOf[OR], ask)
    }
  }


  @Test
  def simpleScenario {
    val repeat = 300 * longRun
    val numberOfClients = tradingSystem.orderReceivers.size

    val bid = new Bid("A1", 100, 1000)
    val ask = new Ask("A1", 100, 1000)
    val orders = bid :: ask :: Nil

    runScenario("simpleScenario", orders, repeat, numberOfClients, 0)
  }

  @Test
  def complexScenario1 {
    complexScenario(tradingSystem.orderReceivers.size)
  }

  @Test
  def complexScenario2 {
    complexScenario(tradingSystem.orderReceivers.size * 2)
  }

  @Test
  def complexScenario4 {
    complexScenario(tradingSystem.orderReceivers.size * 4)
  }

  @Test
  def complexScenario6 {
    complexScenario(tradingSystem.orderReceivers.size * 6)
  }

  @Test
  def complexScenario8 {
    complexScenario(tradingSystem.orderReceivers.size * 8)
  }

  @Test
  def complexScenario10 {
    complexScenario(tradingSystem.orderReceivers.size * 10)
  }

  def complexScenario(numberOfClients: Int) {
    val repeat = 50 * longRun * tradingSystem.orderReceivers.size / numberOfClients

    val prefixes = "A" :: "B" :: "C" :: Nil
    val askOrders = for{
      s <- prefixes
      i <- 1 to 5}
      yield new Ask(s + i, 100 - i, 1000)
    val bidOrders = for{
      s <- prefixes
      i <- 1 to 5}
      yield new Bid(s + i, 100 - i, 1000)
    val orders = askOrders ::: bidOrders

    runScenario("complexScenario", orders, repeat, numberOfClients, 0)
  }

  @Test
  def manyOrderbooks {
    val repeat = 2 * longRun
    val numberOfClients = tradingSystem.orderReceivers.size

    val orderbooks = tradingSystem.allOrderbookSymbols
    val askOrders = for (o <- orderbooks) yield new Ask(o, 100, 1000)
    val bidOrders = for (o <- orderbooks) yield new Bid(o, 100, 1000)
    val orders = askOrders ::: bidOrders

    runScenario("manyOrderbooks", orders, repeat, numberOfClients, 5)
  }

  @Test
  def manyClients {
    val repeat = 1 * longRun
    val numberOfClients = tradingSystem.orderReceivers.size * 10

    val orderbooks = tradingSystem.allOrderbookSymbols
    val askOrders = for (o <- orderbooks) yield new Ask(o, 100, 1000)
    val bidOrders = for (o <- orderbooks) yield new Bid(o, 100, 1000)
    val orders = askOrders ::: bidOrders

    runScenario("manyClients", orders, repeat, numberOfClients, 5)
  }

  @Test
  def oneClient {
    val repeat = 10000 * longRun
    val numberOfClients = 1

    val bid = new Bid("A1", 100, 1000)
    val ask = new Ask("A1", 100, 1000)
    val orders = bid :: ask :: Nil

    runScenario("oneClient", orders, repeat, numberOfClients, 0)
  }

  @Test
  def oneSlowClient {
    val repeat = 300 * longRun
    val numberOfClients = 1

    val bid = new Bid("A1", 100, 1000)
    val ask = new Ask("A1", 100, 1000)
    val orders = bid :: ask :: Nil

    runScenario("oneSlowClient", orders, repeat, numberOfClients, 5)
  }

  def logMeasurement(scenario: String, numberOfClients: Int, durationNs: Long, repeat: Int, totalNumberOfRequests: Int, averageRspTimeNs: Long, maxRspTimeNs: Long) {
    val durationUs = durationNs / 1000
    val durationMs = durationNs / 1000000
    val durationS = durationNs.toDouble / 1000000000.0
    println("# " + getClass.getSimpleName + "." + scenario + "[" + totalNumberOfRequests + "]: "
        + numberOfClients + " clients, "
        + durationMs + " ms, "
        + (averageRspTimeNs / 1000) + " us/order, "
        + (totalNumberOfRequests.toDouble / durationS).formatted("%.0f") + " TPS, "
        + "max " + (maxRspTimeNs / 1000000) + " ms")
  }

  case class Metrics(
      var numberOfRequests: Long,
      var totalRspTimeNs: Long,
      var maxRspTimeNs: Long)



  def delay(delayMs: Int) {
    val adjustedDelay =
    if (delayMs >= 5) {
      val dist = 0.2 * delayMs
      (delayMs + random.nextGaussian * dist).intValue
    } else {
      delayMs
    }

    if (adjustedDelay > 0) {
      Thread.sleep(adjustedDelay)
    }
  }

}
