import scala.util.control.TailCalls.TailRec
import scala.util.Random
import scala.io.Source
import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

object Main extends App {
  implicit val ec: ExecutionContext = ExecutionContext.global
  val names = List("tspa", "tspb")
  val resultsTablePath = Paths.get("results/results_table.txt")
  val resultsBestPath = Paths.get("results/results_best.txt")
  Files.write(
    resultsTablePath,
    "".getBytes,
    StandardOpenOption.CREATE,
    StandardOpenOption.TRUNCATE_EXISTING
  )
  Files.write(
    resultsBestPath,
    "".getBytes,
    StandardOpenOption.CREATE,
    StandardOpenOption.TRUNCATE_EXISTING
  )

  def writeHeader(name: String): Unit = {
    val header =
      s"Instance: $name\n| Method | Min | Avg | Max | Time |\n| --- | --- | --- | --- | --- |\n"
    writeResults(header, resultsTablePath)
  }

  def writeResults(output: String, path: java.nio.file.Path): Unit = {
    Files.write(
      path,
      output.getBytes,
      StandardOpenOption.CREATE,
      StandardOpenOption.APPEND
    )
  }

  def processSolutions(
      instance: String,
      methodName: String,
      problemInstance: ProblemInstance,
      solutionMethod: (ProblemInstance, Int) => Solution
  ): Unit = {
    val startTime = System.nanoTime()
    val solutions = problemInstance.cities.view
      .map(city => Future { solutionMethod(problemInstance, city) })
      .toSeq
    val result = Await.result(Future.sequence(solutions), 3600.seconds)
    val endTime = System.nanoTime()
    val bestSolution = result.minBy(_.cost)
    val distances = result.map(_.cost).toIndexedSeq
    val outputTable =
      f"| `$methodName` | ${distances.min} | ${distances.sum / distances.size} | ${distances.max} | ${(endTime - startTime) / 1e9}%.4f |\n"
    writeResults(outputTable, resultsTablePath)
    val outputBest =
      s"Instance: $instance\nMethod: $methodName\nBest Solution: ${bestSolution}\n\n"
    writeResults(outputBest, resultsBestPath)
  }

  val solutionMethods = List(
    ("random", SolutionFactory.getRandomSolution _),
    ("greedy_tail", SolutionFactory.getGreedyAppendSolution _),
    ("greedy_any_position", SolutionFactory.getGreedyAnyPositionSolution _),
    ("greedy_cycle", SolutionFactory.getGreedyCycleSolution _),
    ("greedy_cycle_regret", SolutionFactory.getGreedyCycleRegretSolution _),
    (
      "greedy_cycle_weighted_regret",
      SolutionFactory.getGreedyCycleWeightedRegretSolution _
    ),
    ("node_exchange_greedy", SolutionFactory.getNodeExhangeGreedySolution _),
    ("node_exchange_steepest", SolutionFactory.getNodeExhangeSteepestSolution _)
  )

  for (name <- names) {
    writeHeader(name)
    val initialData = CSVReader.readCSV(s"${name.toUpperCase()}.csv")
    solutionMethods.foreach { case (suffix, method) =>
      processSolutions(name, suffix, initialData, method)
    }
    writeResults("\n", resultsTablePath)
    writeResults("\n", resultsBestPath)
  }
}
