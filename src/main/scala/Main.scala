import scala.util.control.TailCalls.TailRec
import scala.util.Random
import scala.io.Source
import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.Executors

object Main extends App with MoveGenerator {
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(
    Executors.newFixedThreadPool(5)
  )
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

  val columnWidths = List(30, 15, 15, 15, 20, 15)

  def formatCell(content: String, width: Int): String = {
    if (content.length >= width) content.take(width) // Truncate if too long
    else content + " " * (width - content.length) // Pad with spaces
  }

  def writeHeader(name: String): Unit = {
    val header =
      s"Instance: $name\n" +
        "| " + formatCell("**Method**", columnWidths(0)) +
        " | " + formatCell("**Min**", columnWidths(1)) +
        " | " + formatCell("**Mean**", columnWidths(2)) +
        " | " + formatCell("**Max**", columnWidths(3)) +
        " | " + formatCell("**Time\\* (s)**", columnWidths(4)) + " |\n" +
        "| " + columnWidths.map("-" * _).mkString(" | ") + " |\n"
    writeResults(header, resultsTablePath)
  }

  def writeHeaderWithIterations(name: String): Unit = {
    val header =
      s"Instance: $name\n" +
        "| " + formatCell("**Method**", columnWidths(0)) +
        " | " + formatCell("**Min**", columnWidths(1)) +
        " | " + formatCell("**Mean**", columnWidths(2)) +
        " | " + formatCell("**Max**", columnWidths(3)) +
        " | " + formatCell("**Avg time (s)**", columnWidths(4)) +
        " | " + formatCell("**Iterations**", columnWidths(5)) + " |\n" +
        "| " + columnWidths.map("-" * _).mkString(" | ") + " |\n"
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
      methodName: SolverNames.Value,
      solutionMethod: () => Solution
  ): Unit = {
    val totalTasks =
      if (SolverNames.advancedHeuristics.contains(methodName)) 20 else 200
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
      "| " +
        formatCell(s"`$methodName`", columnWidths(0)) +
        " | " +
        formatCell(distances.min.toString, columnWidths(1)) +
        " | " +
        formatCell((distances.sum / distances.size).toString, columnWidths(2)) +
        " | " +
        formatCell(distances.max.toString, columnWidths(3)) +
        " | " +
        formatCell(f"${averageTimeMS / 1e3}%.4f", columnWidths(4)) +
        " | " +
        formatCell(
          if (avgIterations == 0) "-" else avgIterations.toString,
          columnWidths(5)
        ) +
        " |\n"

    writeResults(outputTable, resultsTablePath)
    val outputBest =
      s"Instance: $instance\nMethod: $methodName\nBest Solution Path: ${bestSolution.path
          .mkString(", ")}\nBest Solution Cost: ${bestSolution.cost}\n\n"
    writeResults(outputBest, resultsBestPath)
    println()
  }

  val solutionMethods = List(
    (
      SolverNames.InsertAnyPositionSolution,
      () => SolutionGenerator.generateInsertAnyPositionSolution
    ),
    (
      SolverNames.MSLS,
      () => SolutionModifier.getMSLS()
    ),
    (
      SolverNames.LNSWithLS,
      () => SolutionModifier.getLargeNeighborhoodSearchWithLocalSearch()
    ),
    (
      SolverNames.LNSWithoutLS,
      () => SolutionModifier.getLargeNeighborhoodSearchWithoutLocalSearch()
    ),
    (
      SolverNames.ILS,
      () =>
        SolutionModifier.getIteratedLocalSearch(
          SolutionGenerator.generateRandomSolution()
        )
    )
  )

  // for (name <- names) {
  //   writeHeaderWithIterations(name)
  //   ProblemInstanceHolder.problemInstance =
  //     CSVReader.readCSV(s"${name.toUpperCase()}.csv")
  //   solutionMethods.foreach { case (suffix, method) =>
  //     processSolutions(name, suffix, method)
  //   }
  //   writeResults("\n", resultsTablePath)
  //   writeResults("\n", resultsBestPath)
  // }

  ProblemInstanceHolder.problemInstance =
    CSVReader.readCSV(s"${names(0).toUpperCase()}.csv")

  val solution = SolutionModifier.getHybridEvolutionaryRandom()
  println(solution.cost)
}

object ProblemInstanceHolder {
  var problemInstance: ProblemInstance = _
}
