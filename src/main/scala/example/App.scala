package example

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.BasicConfigurator;
import java.net._
import java.io._
import scala.io._
import org.json.JSONObject;
import java.util.{ Calendar, Date }
import java.text.SimpleDateFormat
import scala.util.Random

object Hello extends Greeting with App {
  println(greeting)

  val fromCurrency = "EUR"
  val toCurrency = "USD"

  BasicConfigurator.configure()
  val logger = Logger.getRootLogger()
  logger.setLevel(Level.WARN)

  val api = new Api()

  while (true) {

    val dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    val key = dateFormatter.format(new Date())
    val xrate =  Random.nextFloat() /1000 
    println(xrate.toString)
    val convertedResult = f"""{\"datetime\":\"$key\",\"xrate\":${1.12460640 +xrate}%.8f}"""
    println(convertedResult)
    writeToSocket(convertedResult)

    /*
    val result = api.getData(fromCurrency, toCurrency)
    if (!result.isEmpty()) {
      /*
      //      val result2 = result.replaceAll("\\s", "");
      val result2 = result.replaceAll("\n", "");
      println(result2)
      //{    "Realtime Currency Exchange Rate": {        "1. From_Currency Code": "EUR",        "2. From_Currency Name": "Euro",        "3. To_Currency Code": "USD",        "4. To_Currency Name": "United States Dollar",        "5. Exchange Rate": "1.12397430",        "6. Last Refreshed": "2019-04-19 04:41:57",        "7. Time Zone": "UTC"    }}
      //{"RealtimeCurrencyExchangeRate":{"1.From_CurrencyCode":"EUR","2.From_CurrencyName":"Euro","3.To_CurrencyCode":"USD","4.To_CurrencyName":"UnitedStatesDollar","5.ExchangeRate":"1.12765000","6.LastRefreshed":"2019-04-1016:34:02","7.TimeZone":"UTC"}}
      writeToSocket(result2)
*/
      println(result)
      val json = (new JSONObject(result)).getJSONObject("Realtime Currency Exchange Rate");
      //      val convertedResult = s"${fromCurrency}_${toCurrency},${json.get("5. Exchange Rate")},${json.get("6. Last Refreshed")}"
//      val result2 = s"${json.get("6. Last Refreshed")},${json.get("5. Exchange Rate")}"
      val convertedResult = f"""{\"datetime\":\"${json.get("6. Last Refreshed")}\",\"xrate\":\"${json.get("5. Exchange Rate")}\"}"""

      println(convertedResult)
      writeToSocket(convertedResult)

    }
    */
    //    Thread.sleep(60000)
    Thread.sleep(20000)
  }

  private def writeToSocket(result: String) {
    try {
      val s = new Socket(InetAddress.getByName("localhost"), 9999)
      lazy val in = new BufferedSource(s.getInputStream()).getLines()
      val out = new PrintStream(s.getOutputStream())

      out.println(result)
      out.flush()
      println("Received: " + in.next())
      s.close()
    } catch {
      case _: Throwable => println("Cannot send to localhost:9999")
    }

  }
}

trait Greeting {
  lazy val greeting: String = "Start to get exchange rate from API"
}
