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

  val names = List("tspa", "tspb").map(_.toUpperCase)

  for (name <- names) {
    println(s"${name.toUpperCase()}")
    val initialData = CSVReader.readCSV(s"${name}.csv")

    // Random solutions
    println("random solutions")
    val randomSolutions =
      (1 to 200).map(_ => SolutionFactory.getRandomSolution(initialData))
    var bestSolution = randomSolutions
      .flatMap {
        case Right(value) => Seq(value)
        case _            => Seq()
      }
      .minBy(_.cost)
    var distances = randomSolutions.map {
      case Left(value)  => value.cost
      case Right(value) => value.cost
    }.toIndexedSeq
    println(
      s"Random min: ${distances.min}, avg: ${distances.sum / distances.size}, max: ${distances.max}"
    )
    TXTWriter.writeTXT(s"${name}_random.txt", bestSolution)

    // Greedy append
    println("greedy append")
    val greedyAppend = initialData.cities
      .map(city =>
        Future {
          SolutionFactory.getGreedyAppendSolution(initialData, city)
        }
      )
      .toSeq
    val greedyAppendResult =
      Await.result(Future.sequence(greedyAppend), 30.seconds)
    bestSolution = greedyAppendResult
      .flatMap {
        case Right(value) => Seq(value)
        case _            => Seq()
      }
      .minBy(_.cost)
    distances = greedyAppendResult.map {
      case Left(value)  => value.cost
      case Right(value) => value.cost
    }.toIndexedSeq
    println(
      s"Greedy append min: ${distances.min}, avg: ${distances.sum / distances.size}, max: ${distances.max}"
    )
    TXTWriter.writeTXT(s"${name}_greedy_append.txt", bestSolution)

    // Greedy at any position
    println("greedy at any position")
    val greedyAtAnyPosition = initialData.cities
      .map(city =>
        Future {
          SolutionFactory.getGreedyAnyPositionSolution(initialData, city)
        }
      )
      .toSeq
    val greedyAtAnyPositionResult =
      Await.result(Future.sequence(greedyAtAnyPosition), 30.seconds)
    bestSolution = greedyAtAnyPositionResult
      .flatMap {
        case Right(value) => Seq(value)
        case _            => Seq()
      }
      .minBy(_.cost)
    distances = greedyAtAnyPositionResult.map {
      case Left(value)  => value.cost
      case Right(value) => value.cost
    }.toIndexedSeq
    println(
      s"Greedy at any position min: ${distances.min}, avg: ${distances.sum / distances.size}, max: ${distances.max}"
    )
    TXTWriter.writeTXT(s"${name}_greedy_at_any_position.txt", bestSolution)

    // Greedy cycle
    println("greedy cycle")
    val greedyCycle = initialData.cities
      .map(city =>
        Future {
          SolutionFactory.getGreedyCycleSolution(initialData, city)
        }
      )
      .toSeq
    val greedyCycleResult =
      Await.result(Future.sequence(greedyCycle), 30.seconds)
    bestSolution = greedyCycleResult
      .flatMap {
        case Right(value) => Seq(value)
        case _            => Seq()
      }
      .minBy(_.cost)
    distances = greedyCycleResult.map {
      case Left(value)  => value.cost
      case Right(value) => value.cost
    }.toIndexedSeq
    println(
      s"Greedy cycle min: ${distances.min}, avg: ${distances.sum / distances.size}, max: ${distances.max}"
    )
    TXTWriter.writeTXT(s"${name}_greedy_cycle.txt", bestSolution)
  }
}
