package example

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.BasicConfigurator;
import java.text.SimpleDateFormat
import java.util.Date

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.PrintWriter;

object Hello extends Greeting with App {
  println(greeting)
  
  val dateFormatter = new SimpleDateFormat("hhmm_ddMMyyyy")
  
  BasicConfigurator.configure()
  val logger = Logger.getRootLogger()
  logger.setLevel(Level.WARN)  
  
  val api = new Api()
  
  val conf = new Configuration()
  conf.set("fs.defaultFS", "hdfs://quickstart.cloudera:8020")
  
  while (true) {
    val dt = dateFormatter.format(new Date())
    val result = api.getData("USD", "JPY")
    println(result)
    if (!result.isEmpty()) {
      writeToHDFS(result, dt)
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
}

trait Greeting {
  lazy val greeting: String = "hello"
}
