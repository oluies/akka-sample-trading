package org.samples.trading.common

import java.util.Random
import org.junit._
import Assert._
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics
import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics

import org.samples.trading.domain._

trait PerformanceTest {

  //	jvm parameters
  //	-server -Xms512m -Xmx1024m -XX:+UseConcMarkSweepGC -Dbenchmark=true

  // Use longRun for benchmark
  val longRun = if (isBenchmark) 50 else 10;
  
  def isBenchmark() = 
    System.getProperty("benchmark") == "true" 
  
  
  var stat: DescriptiveStatistics = _

  type TS <: TradingSystem

  var tradingSystem: TS = _
  val random: Random = new Random()

  def createTradingSystem: TS

  def placeOrder(orderReceiver: TS#OR, order: Order): Rsp

  def runScenario(scenario: String, orders: List[Order], repeat: Int, numberOfClients: Int, delayMs: Int)

  @Before
  def setUp {
    stat = new SynchronizedDescriptiveStatistics
    tradingSystem = createTradingSystem
    tradingSystem.start
    warmUp
    TotalTradeCounter.reset
    stat = new SynchronizedDescriptiveStatistics
  }

  @After
  def tearDown {
    tradingSystem.shutdown
  }

  def warmUp {
    val bid = new Bid("A1", 100, 1000)
    val ask = new Ask("A1", 100, 1000)

    val orderReceiver = tradingSystem.orderReceivers.head
    for (i <- 1 to 10) {
      placeOrder(orderReceiver, bid)
      placeOrder(orderReceiver, ask)
    }
  }


  @Test
  @Ignore
  def simpleScenario {
    val repeat = 300 * longRun
    val numberOfClients = tradingSystem.orderReceivers.size

    val bid = new Bid("A1", 100, 1000)
    val ask = new Ask("A1", 100, 1000)
    val orders = bid :: ask :: Nil

    runScenario("simpleScenario", orders, repeat, numberOfClients, 0)
  }

  @Test def complexScenario1 = complexScenario(1)
  @Test def complexScenario2 = complexScenario(2)
  @Test def complexScenario4 = complexScenario(4)
  @Test def complexScenario6 = complexScenario(6)
  @Test def complexScenario8 = complexScenario(8)
  @Test def complexScenario10 = complexScenario(10)
  @Test def complexScenario20 = complexScenario(20)
  @Test def complexScenario40 = complexScenario(40)
  @Test def complexScenario60 = complexScenario(60)
  @Test def complexScenario80 = complexScenario(80)
  @Test def complexScenario100 = complexScenario(100)
  @Ignore @Test def complexScenario200 = complexScenario(200)
  @Ignore @Test def complexScenario300 = complexScenario(300)
  @Ignore @Test def complexScenario400 = complexScenario(400)
  

  def complexScenario(numberOfClients: Int) {
    val repeat = 500 * longRun / numberOfClients

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

    runScenario("benchmark", orders, repeat, numberOfClients, 0)
  }

  @Test
  @Ignore
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
  @Ignore
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
  @Ignore
  def oneClient {
    val repeat = 10000 * longRun
    val numberOfClients = 1

    val bid = new Bid("A1", 100, 1000)
    val ask = new Ask("A1", 100, 1000)
    val orders = bid :: ask :: Nil

    runScenario("oneClient", orders, repeat, numberOfClients, 0)
  }

  @Test
  @Ignore
  def oneSlowClient {
    val repeat = 300 * longRun
    val numberOfClients = 1

    val bid = new Bid("A1", 100, 1000)
    val ask = new Ask("A1", 100, 1000)
    val orders = bid :: ask :: Nil

    runScenario("oneSlowClient", orders, repeat, numberOfClients, 5)
  }

  def logMeasurement(scenario: String, numberOfClients: Int, durationNs: Long, repeat: Int, totalNumberOfRequests: Int) {
    val durationUs = durationNs / 1000
    val durationMs = durationNs / 1000000
    val durationS = durationNs.toDouble / 1000000000.0
    val duration = durationS.formatted("%.0f")
    val n = stat.getN
    val mean = (stat.getMean / 1000).formatted("%.0f")
    val tps = (stat.getN.toDouble / durationS).formatted("%.0f")
    val p5 = (stat.getPercentile(5.0) / 1000).formatted("%.0f")
    val p25 = (stat.getPercentile(25.0) / 1000).formatted("%.0f")
    val p50 = (stat.getPercentile(50.0) / 1000).formatted("%.0f")
    val p75 = (stat.getPercentile(75.0) / 1000).formatted("%.0f")
    val p95 = (stat.getPercentile(95.0) / 1000).formatted("%.0f")
    val name = getClass.getSimpleName + "." + scenario
    
    val summaryLine = name :: numberOfClients.toString :: tps :: mean :: p5 :: p25 :: p50 :: p75 :: p95 :: duration :: n :: Nil
    StatSingleton.results = summaryLine.mkString("\t") :: StatSingleton.results
    

    val spaces = "                                                                                     "
    val headerScenarioCol = ("Scenario" + spaces).take(name.length)
    
    val headerLine =  (headerScenarioCol ::        "clients" :: "TPS" :: "mean" :: "5%  " :: "25% " :: "50% " :: "75% " :: "95% " :: "Durat." :: "N" :: Nil)
      .mkString("\t")
    val headerLine2 = (spaces.take(name.length) :: "       " :: "   " :: "(us)" :: "(us)" :: "(us)" :: "(us)" :: "(us)" :: "(us)" :: "(s)   " :: " " :: Nil)
      .mkString("\t")
    val line = List.fill(StatSingleton.results.head.replaceAll("\t", "      ").length)("-").mkString 
    println(line.replace('-', '='))
    println(headerLine)
    println(headerLine2)
    println(line)
    println(StatSingleton.results.reverse.mkString("\n"))
    println(line)
  }

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

object StatSingleton {
  var results: List[String] = Nil
}
