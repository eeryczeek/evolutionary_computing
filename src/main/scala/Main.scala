import scala.util.control.TailCalls.TailRec
import scala.util.Random
import scala.io.Source
import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import java.util.concurrent.atomic.AtomicInteger

object Main extends App with MoveGenerator {
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

  def writeHeaderWithIterations(name: String): Unit = {
    val header =
      s"Instance: $name\n| **Method** | **Min** | **Mean** | **Max** | **Avg time (s)** | **Iterations** |\n| --- | --- | --- | --- | --- | --- |\n"
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
      solutionMethod: () => Solution
  ): Unit = {
    val totalTasks = 20 // ProblemInstanceHolder.problemInstance.cities.size
    val completedTasks = new AtomicInteger(0)

    val solutions = (1 to totalTasks)
      .map(city =>
        Future {
          val startTime = System.currentTimeMillis()
          val solution = solutionMethod()
          val endTime = System.currentTimeMillis()
          val completed = completedTasks.incrementAndGet()
          print(s"\rprocessing $methodName [$completed/$totalTasks]")
          (solution, endTime - startTime)
        }
      )
      .toSeq

    val result = Await.result(Future.sequence(solutions), 3600.seconds)
    val resultsSolutions = result.map(_._1)
    val averageTimeMS = result.map(_._2).sum / totalTasks
    val bestSolution = resultsSolutions.minBy(_.cost)
    val distances = resultsSolutions.map(_.cost).toIndexedSeq
    val iterations =
      resultsSolutions.flatMap(_.additionalData).flatMap(_.numOfIterations)
    val avgIterations =
      if (iterations.isEmpty) 0 else iterations.sum / iterations.size
    val outputTable =
      f"| `$methodName` | ${distances.min} | ${distances.sum / distances.size} | ${distances.max} | ${averageTimeMS / 1e3}%.4f | $avgIterations |\n"
    writeResults(outputTable, resultsTablePath)
    val outputBest =
      s"Instance: $instance\nMethod: $methodName\nBest Solution Path: ${bestSolution.path
          .mkString(", ")}\nBest Solution Cost: ${bestSolution.cost}\n\n"
    writeResults(outputBest, resultsBestPath)
    println()
  }

  val solutionMethods = List(
    // ("RandomSolution", () => SolutionGenerator.generateRandomSolution),
    // ("TailAppendSolution", () => SolutionGenerator.generateTailAppendSolution),
    // (
    //   "InsertAnyPositionSolution",
    //   () => SolutionGenerator.generateInsertAnyPositionSolution
    // ),
    // ("CycleSolution", () => SolutionGenerator.generateCycleSolution),
    // (
    //   "CycleRegretSolution",
    //   () => SolutionGenerator.generateCycleRegretSolution
    // ),
    // (
    //   "CycleWeightedRegretSolution",
    //   () => SolutionGenerator.generateCycleWeightedRegretSolution
    // ),
    (
      "LargeNeighborhoodSearchWithLS",
      () => SolutionModifier.getLargeNeighborhoodSearchWithLocalSearch()
    ),
    (
      "LargeNeighborhoodSearchWithoutLS",
      () => SolutionModifier.getLargeNeighborhoodSearchWithoutLocalSearch()
    ),
    (
      "IteratedLocalSearch",
      () =>
        SolutionModifier.getIteratedLocalSearch(
          SolutionGenerator.generateRandomSolution()
        )
    ),
    (
      "MSLS",
      () => SolutionModifier.getMSLS()
    )
  )

  for (name <- names) {
    writeHeaderWithIterations(name)
    ProblemInstanceHolder.problemInstance =
      CSVReader.readCSV(s"${name.toUpperCase()}.csv")
    solutionMethods.foreach { case (suffix, method) =>
      processSolutions(name, suffix, method)
    }
    writeResults("\n", resultsTablePath)
    writeResults("\n", resultsBestPath)
  }
}

object ProblemInstanceHolder {
  var problemInstance: ProblemInstance = _
}
