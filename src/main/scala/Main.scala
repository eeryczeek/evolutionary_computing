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
  val names = List("tspa", "tspb")

  def processSolutions(
      name: String,
      problemInstance: ProblemInstance,
      solutionMethod: (ProblemInstance, Int) => FullSolution,
      fileNameSuffix: String
  ): Unit = {
    val startTime = System.nanoTime()
    val solutions = problemInstance.cities.view
      .map(city => Future { solutionMethod(problemInstance, city) })
      .toSeq
    val result = Await.result(Future.sequence(solutions), 30.seconds)
    val endTime = System.nanoTime()
    val bestSolution = result.minBy(_.cost)
    val distances = result.map(_.cost).toIndexedSeq

    println(
      s"$fileNameSuffix min: ${distances.min}, avg: ${distances.sum / distances.size}, max: ${distances.max}, time: ${(endTime - startTime) / 1e9} seconds"
    )
    TXTWriter.writeTXT(s"results/${name}_$fileNameSuffix.txt", bestSolution)
  }

  for (name <- names) {
    println(s"${name.toUpperCase()}")
    val initialData = CSVReader.readCSV(s"${name.toUpperCase()}.csv")

    // Random solutions
    processSolutions(
      name,
      initialData,
      SolutionFactory.getRandomSolution _,
      "random"
    )

    // Greedy append
    processSolutions(
      name,
      initialData,
      SolutionFactory.getGreedyAppendSolution _,
      "greedy_tail"
    )

    // Greedy at any position
    processSolutions(
      name,
      initialData,
      SolutionFactory.getGreedyAnyPositionSolution _,
      "greedy_any_position"
    )

    // Greedy cycle
    processSolutions(
      name,
      initialData,
      SolutionFactory.getGreedyCycleSolution _,
      "greedy_cycle"
    )

    // Greedy cycle with regret
    processSolutions(
      name,
      initialData,
      SolutionFactory.getGreedyCycleRegretSolution _,
      "greedy_cycle_regret"
    )

    processSolutions(
      name,
      initialData,
      SolutionFactory.getGreedyCycleWeightedRegretSolution _,
      "greedy_cycle_weighted_regret"
    )
  }
}
