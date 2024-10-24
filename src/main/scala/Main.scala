import scala.util.control.TailCalls.TailRec
import scala.util.Random
import scala.io.Source
import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import java.util.concurrent.atomic.AtomicInteger

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
      s"Instance: $name\n| **Method** | **Min** | **Mean** | **Max** | **Time\\* (s)** |\n| --- | --- | --- | --- | --- |\n"
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
    val totalTasks = problemInstance.cities.size
    val completedTasks = new AtomicInteger(0)

    val solutions = problemInstance.cities.toList.view
      .map(city =>
        Future {
          val solution = solutionMethod(problemInstance, city)
          val completed = completedTasks.incrementAndGet()
          print(s"\rprocessing $methodName [$completed/$totalTasks]")
          solution
        }
      )
      .toSeq

    val result = Await.result(Future.sequence(solutions), 3600.seconds)
    val endTime = System.nanoTime()
    val bestSolution = result.minBy(_.cost)
    val distances = result.map(_.cost).toIndexedSeq
    val outputTable =
      f"| `$methodName` | ${distances.min} | ${distances.sum / distances.size} | ${distances.max} | ${(endTime - startTime) / 1e9}%.4f |\n"
    writeResults(outputTable, resultsTablePath)
    val outputBest =
      s"Instance: $instance\nMethod: $methodName\nBest Solution Path: ${bestSolution.path
          .mkString(", ")}\nBest Solution Cost: ${bestSolution.cost}\n\n"
    writeResults(outputBest, resultsBestPath)
    println()
  }

  val solutionMethods = List(
    ("random", SolutionFactory.getRandomSolution _),
    ("greedy_tail", SolutionFactory.getGreedyTailSolution _),
    ("greedy_any_position", SolutionFactory.getGreedyAnyPositionSolution _),
    ("greedy_cycle", SolutionFactory.getGreedyCycleSolution _),
    ("regret", SolutionFactory.getGreedyCycleRegretSolution _),
    ("weighted_regret", SolutionFactory.getGreedyCycleWeightedRegretSolution _),
    (
      "ls_edges_greedy_random",
      SolutionFactory.getLocalSearchWithEdgesSwapsGreedyRandomStart _
    ),
    (
      "ls_edges_greedy_heuristics",
      SolutionFactory.getLocalSearchWithEdgesSwapsGreedyHeuristicStart _
    ),
    (
      "ls_edges_steepest_random",
      SolutionFactory.getLocalSearchWithEdgesSwapsSteepestRandomStart _
    ),
    (
      "ls_edges_steepest_heuristics",
      SolutionFactory.getLocalSearchWithEdgesSwapsSteepestHeuristicStart _
    ),
    (
      "ls_nodes_greedy_random",
      SolutionFactory.getLocalSearchWithNodesSwapsGreedyRandomStart _
    ),
    (
      "ls_nodes_greedy_heuristics",
      SolutionFactory.getLocalSearchWithNodesSwapsGreedyHeuristicStart _
    ),
    (
      "ls_nodes_steepest_random",
      SolutionFactory.getLocalSearchWithNodesSwapsSteepestRandomStart _
    ),
    (
      "ls_nodes_steepest_heuristics",
      SolutionFactory.getLocalSearchWithNodesSwapsSteepestHeuristicStart _
    )
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
