import scala.concurrent.Await
import scala.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import java.nio.file.Paths
import java.nio.file.Files
import java.nio.file.StandardOpenOption
object ExperimentLab8 extends App with MoveGenerator {
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(
    Executors.newFixedThreadPool(10)
  )

  val names = List("tspa", "tspb")

  def writeCSVHeader(name: String): Unit = {
    val header =
      "cost,similarityEdgesAvg,similarityNodesAvg,similarityEdgesBest,similarityNodesBest\n"
    writeResults(header, Paths.get(s"results/${name}_similarity.csv"))
  }

  def clearFile(path: java.nio.file.Path): Unit = {
    Files.write(
      path,
      "".getBytes,
      StandardOpenOption.CREATE,
      StandardOpenOption.TRUNCATE_EXISTING
    )
  }

  def writeResults(output: String, path: java.nio.file.Path): Unit = {
    Files.write(
      path,
      output.getBytes,
      StandardOpenOption.CREATE,
      StandardOpenOption.APPEND
    )
  }

  def similarity1(solution1: Solution, solution2: Solution): Int = {
    def pairsEqual(pair1: Pair, pair2: Pair): Boolean = {
      (pair1.city1 == pair2.city1 && pair1.city2 == pair2.city2) ||
      (pair1.city1 == pair2.city2 && pair1.city2 == pair2.city1)
    }
    val pairs1 = getPathConsecutivePairs(solution1.path).toSet
    val pairs2 = getPathConsecutivePairs(solution2.path).toSet
    val common = pairs1
      .filter(p1 => pairs2.exists(p2 => pairsEqual(p1, p2)))
      .size
    common
  }

  def similarity2(solution1: Solution, solution2: Solution): Int = {
    val path1 = solution1.path
    val path2 = solution2.path
    path1.intersect(path2).size
  }

  def showSimilarities(name: String): Unit = {
    ProblemInstanceHolder.problemInstance =
      CSVReader.readCSV(s"${name.toUpperCase()}.csv")

    val path = Paths.get(s"results/${name}_similarity.csv")

    clearFile(path)
    writeCSVHeader(name)
    val counter = new AtomicInteger(0)
    val futures = for (_ <- 1 to 1000) yield Future {
      val solution = SolutionModifier.getLocalSearchGreedy(
        SolutionGenerator.generateRandomSolution()
      )
      val i = counter.incrementAndGet()
      print(s"\rprocessing $name [$i/1000]")
      solution
    }

    val solutions = Await.result(Future.sequence(futures), 600.seconds)
    println("finding best solution")
    val bestSolution =
      SolutionModifier.getLargeNeighborhoodSearchWithLocalSearch()
    println()
    counter.set(0)
    val similarities = solutions
      .map { solution1 =>
        Future {
          val similarities = solutions.map { solution2 =>
            (
              similarity1(solution1, solution2),
              similarity2(solution1, solution2)
            )
          }

          val (avgSim1, avgSim2) = similarities
            .reduce((a, b) => (a._1 + b._1, a._2 + b._2))

          val i = counter.incrementAndGet()
          print(s"\rprocessing similarities $name [$i/1000]")
          solution1.cost ->
            (avgSim1.toDouble / 1000,
            avgSim2.toDouble / 1000,
            similarity1(solution1, bestSolution).toDouble,
            similarity2(solution1, bestSolution).toDouble)
        }
      }

    Await
      .result(Future.sequence(similarities), 600.seconds)
      .groupBy(_._1)
      .mapValues(vals => {
        val (sim1, sim2, simBest1, simBest2) =
          vals
            .map(_._2)
            .reduce((a, b) =>
              (a._1 + b._1, a._2 + b._2, a._3 + b._3, a._4 + b._4)
            )
        (
          sim1 / vals.size,
          sim2 / vals.size,
          simBest1 / vals.size,
          simBest2 / vals.size
        )
      })
      .toSeq
      .sortBy(_._1)
      .foreach { case (cost, (avgSim1, avgSim2, simBest1, simBest2)) =>
        Main.writeResults(
          s"$cost,$avgSim1,$avgSim2,$simBest1,$simBest2\n",
          path
        )
      }

    println()
  }

  for (name <- names) {
    showSimilarities(name)
  }
}
