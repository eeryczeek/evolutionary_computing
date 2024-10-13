import scala.util.control.TailCalls.TailRec
import scala.util.Random
import scala.io.Source
import java.nio.file.Files
import java.nio.file.Paths
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

object Main extends App {
  implicit val ec: ExecutionContext = ExecutionContext.global
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

  // greedy at the end
  println("Starting greedy appends")
  val greedyAppend = initialData.cities
    .map(city =>
      Future {
        print("|")
        SolutionFactory.getGreedyAppendSolution(initialData, city)
      }
    )
    .toSeq
  val greedyAppendResult =
    Await.result(Future.sequence(greedyAppend), 30.seconds)
  var distances = greedyAppendResult.map {
    case Left(value)  => value.cost
    case Right(value) => value.cost
  }
  println(
    s"\nGreedy append min: ${distances.min}, avg: ${distances.sum / distances.size}, max: ${distances.max}"
  )

  var randomSolutions =
    (1 to 200).map(_ => SolutionFactory.getRandomSolution(initialData))
  distances = randomSolutions.map {
    case Left(value)  => value.cost
    case Right(value) => value.cost
  }
  println(
    s"Random append min: ${distances.min}, avg: ${distances.sum / distances.size}, max: ${distances.max}"
  )

  // greedy at any position
  println("Starting greedy at any position")
  val greedyAtAnyPosition = initialData.cities
    .map(city =>
      Future {
        print("|")
        SolutionFactory.getGreedyAnyPositionSolution(initialData, city)
      }
    )
    .toSeq
  val greedyAtAnyPositionResult =
    Await.result(Future.sequence(greedyAtAnyPosition), 30.seconds)
  distances = greedyAtAnyPositionResult.map {
    case Left(value)  => value.cost
    case Right(value) => value.cost
  }
  println(
    s"\nGreedy at any position min: ${distances.min}, avg: ${distances.sum / distances.size}, max: ${distances.max}"
  )

  randomSolutions =
    (1 to 200).map(_ => SolutionFactory.getRandomSolution(initialData))
  distances = randomSolutions.map {
    case Left(value)  => value.cost
    case Right(value) => value.cost
  }
  println(
    s"Random append min: ${distances.min}, avg: ${distances.sum / distances.size}, max: ${distances.max}"
  )

  // greedy cycle
  println("Starting greedy cycle")
  val greedyCycle = initialData.cities
    .map(city =>
      Future {
        print("|")
        SolutionFactory.getGreedyCycleSolution(initialData, city)
      }
    )
    .toSeq
  val greedyCycleResult =
    Await.result(Future.sequence(greedyAppend), 30.seconds)
  distances = greedyCycleResult.map {
    case Left(value)  => value.cost
    case Right(value) => value.cost
  }
  println(
    s"\nGreedy at any position min: ${distances.min}, avg: ${distances.sum / distances.size}, max: ${distances.max}"
  )

  randomSolutions =
    (1 to 200).map(_ => SolutionFactory.getRandomSolution(initialData))
  distances = randomSolutions.map {
    case Left(value)  => value.cost
    case Right(value) => value.cost
  }
  println(
    s"Random append min: ${distances.min}, avg: ${distances.sum / distances.size}, max: ${distances.max}"
  )
}
