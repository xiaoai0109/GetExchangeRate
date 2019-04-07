package example

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.BasicConfigurator;
import java.text.SimpleDateFormat
import java.util.Date
import org.json.JSONObject;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.PrintWriter;

import java.net._
import java.io._
import scala.io._

object Hello extends Greeting with App {
  println(greeting)
  
  val dateFormatter = new SimpleDateFormat("hhmm_ddMMyyyy")
  val fromCurrency = "EUR"
  val toCurrency = "USD"
  
  BasicConfigurator.configure()
  val logger = Logger.getRootLogger()
  logger.setLevel(Level.WARN)  
  
  val api = new Api()
  
  val conf = new Configuration()
  conf.set("fs.defaultFS", "hdfs://quickstart.cloudera:8020")
  
  while (true) {
    val dt = dateFormatter.format(new Date())
    val result = api.getData(fromCurrency, toCurrency)
    println(result)
    if (!result.isEmpty()) {
      val result2 = cleanseJson(result)
      writeToSocket(result2)      
//      writeToHDFS(result, dt)

    }
    Thread.sleep(60000)
  }
  
  private def writeToHDFS(result: String, dt: String) {
    val fs = FileSystem.get(conf)
    val pathStr = "/user/cloudera/xrate/" + dt + ".json"
    val os = fs.create(new Path(pathStr))
    println("start to write")
    os.write(result.getBytes)
    val os2 = fs.create(new Path("/user/cloudera/xrate/" + "latest.txt"))
    os2.write(pathStr.getBytes)
    fs.close()
  }
  
  private def writeToSocket(result: String) {
    val s = new Socket(InetAddress.getByName("localhost"), 9999)
    lazy val in = new BufferedSource(s.getInputStream()).getLines()
    val out = new PrintStream(s.getOutputStream())

    out.println(result)
    out.flush()
    println("Received: " + in.next())
    s.close()
  }
  
  private def cleanseJson(result: String): String = {
    val json = (new JSONObject(result)).getJSONObject("Realtime Currency Exchange Rate");
    val xrate = json.getDouble("5. Exchange Rate")
    //{"Currency":"EUR_USD","ExchangeRate":"1.12183","RefreshedTime":"2019-04-07 16:59:56"}
    val convertedResult = f"""{\"Currency\":\"${fromCurrency}_${toCurrency}\",\"ExchangeRate\":\"$xrate%.5f\",\"RefreshedTime\":\"${json.get("6. Last Refreshed")}\"}"""
    println(convertedResult)
    convertedResult
  }
}

trait Greeting {
  lazy val greeting: String = "hello"
}
