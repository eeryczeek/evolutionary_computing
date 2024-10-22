import scala.annotation.tailrec

object GreedyCycleSolution {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    if (currentSolution.path.size == problemInstance.expectedSolutionLen) {
      return (currentSolution, availableCities)
    }
    val currentCycle = currentSolution.path
    val distances = problemInstance.distances

    val (cityToInsertAfter, cityToInsert, additionalCost) = currentCycle
      .zip(currentCycle.tail :+ currentCycle.head)
      .view
      .map { case (city1, city2) =>
        val (bestCity, additionalDistance) =
          findBestMiddleCity(
            city1,
            city2,
            distances,
            problemInstance.cityCosts,
            availableCities
          )
        (city1, bestCity, additionalDistance)
      }
      .minBy(_._3)

    val insertIndex = currentCycle.indexOf(cityToInsertAfter)
    val newCycle = currentCycle.take(insertIndex + 1) ++ Array(
      cityToInsert
    ) ++ currentCycle.drop(insertIndex + 1)
    (
      Solution(
        newCycle,
        currentSolution.cost + additionalCost
      ),
      availableCities - cityToInsert
    )
  }

  def findBestMiddleCity(
      city1: Int,
      city2: Int,
      distances: Array[Array[Int]],
      cityCosts: Array[Int],
      citiesToChooseFrom: Set[Int]
  ): (Int, Int) = {
    citiesToChooseFrom.view
      .map { middleCity =>
        (
          middleCity,
          distances(city1)(middleCity) +
            distances(middleCity)(city2) +
            cityCosts(middleCity) -
            distances(city1)(city2)
        )
      }
      .minBy(_._2)
  }
}
