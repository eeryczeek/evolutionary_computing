import scala.util.control.TailCalls.TailRec
import scala.util.Random
import scala.io.Source
import java.nio.file.Files
import java.nio.file.Paths

object Main extends App {
  val dataFilePath = Option(System.getProperty("data"))
  if (dataFilePath.isEmpty) {
    println("Proper use: sbt run -Ddata=[TSPA.csv|TSPB.csv]")
    System.exit(-1)
  }
  dataFilePath match {
    case None =>
      println("Proper use: sbt run -Ddata=[TSPA.csv|TSPB.csv]")
      System.exit(-1)
    case Some(path) if !Files.exists(Paths.get(path)) =>
      println("Data file does not exists")
      System.exit(-1)
    case Some(_) =>
  }

  val initialData = CSVReader.readCSV(dataFilePath.get)
}
