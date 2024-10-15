import scala.annotation.tailrec

object GreedyCycleSolution {
  @tailrec
  def generate(
      problemInstance: ProblemInstance,
      currentSolution: PartialSolution,
      availableCities: Set[Int]
  ): FullSolution = {
    if (currentSolution.path.size == problemInstance.expectedSolutionLen) {
      FullSolution(
        currentSolution.path,
        currentSolution.cost + problemInstance.distances(
          currentSolution.path.last
        )(currentSolution.path.head)
      )
    } else {
      val (newSolution, newAvailableCities) = updateSolution(
        problemInstance,
        currentSolution,
        availableCities
      )
      generate(
        problemInstance,
        newSolution,
        newAvailableCities
      )
    }
  }

  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: PartialSolution,
      availableCities: Set[Int]
  ): (PartialSolution, Set[Int]) = {
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
      PartialSolution(
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
      citiesToChooseFrom: Set[Int]
  ): (Int, Int) = {
    citiesToChooseFrom.view
      .map { middleCity =>
        (
          middleCity,
          distances(city1)(middleCity) +
            distances(middleCity)(city2) -
            distances(city1)(city2)
        )
      }
      .minBy(_._2)
  }
}
