import scala.annotation.tailrec

object GreedyAtAnyPositionSolution {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    if (currentSolution.path.size == problemInstance.expectedSolutionLen) {
      return (currentSolution, availableCities)
    }
    val path = currentSolution.path
    val distances = problemInstance.distances
    val cityCosts = problemInstance.cityCosts
    val pathIndices = path.zipWithIndex.toMap

    val (bestCity, bestCost, insertPosition) =
      availableCities.foldLeft((Int.MinValue, Int.MaxValue, -1)) {
        case ((bestCity, bestCost, insertPosition), city) =>
          val prependCost = distances(city)(path.head) + cityCosts(city)
          val appendCost = distances(path.last)(city) + cityCosts(city)

          val (newBestCity, newBestCost, newInsertPosition) =
            if (prependCost < bestCost) (city, prependCost, -1)
            else if (appendCost < bestCost) (city, appendCost, path.size)
            else (bestCity, bestCost, insertPosition)

          val (finalBestCity, finalBestCost, finalInsertPosition) = path
            .zip(path.tail)
            .foldLeft((newBestCity, newBestCost, newInsertPosition)) {
              case ((bestCity, bestCost, insertPosition), (city1, city2)) =>
                val cost = distances(city1)(city) +
                  distances(city)(city2) +
                  cityCosts(city) -
                  distances(city1)(city2)
                if (cost < bestCost) (city, cost, pathIndices(city1))
                else (bestCity, bestCost, insertPosition)
            }

          (finalBestCity, finalBestCost, finalInsertPosition)
      }

    val newPath = insertPosition match {
      case -1                      => bestCity +: path
      case pos if pos == path.size => path :+ bestCity
      case pos => path.take(pos + 1) ++ Array(bestCity) ++ path.drop(pos + 1)
    }

    val newSolution = Solution(
      newPath,
      currentSolution.cost + bestCost +
        distances(newPath.last)(newPath.head) -
        distances(path.last)(path.head)
    )

    (newSolution, availableCities - bestCity)
  }
}
