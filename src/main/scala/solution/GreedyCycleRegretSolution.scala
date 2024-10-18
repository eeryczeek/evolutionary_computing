import scala.annotation.tailrec

object GreedyCycleRegretSolution {
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
          findBestMiddleCityWithRegret(
            city1,
            city2,
            distances,
            availableCities
          )
        (city1, bestCity, additionalDistance)
      }
      .minBy(_._3)

    val insertIndex = currentCycle.indexOf(cityToInsertAfter)
    val newCycle = currentCycle.take(insertIndex + 1) ++ List(
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

  def findBestMiddleCityWithRegret(
      city1: Int,
      city2: Int,
      distances: Array[Array[Int]],
      citiesToChooseFrom: Set[Int]
  ): (Int, Int) = {

    val insertionCosts = citiesToChooseFrom.view
      .map { middleCity =>
        (
          middleCity,
          distances(city1)(middleCity) +
            distances(middleCity)(city2) -
            distances(city1)(city2)
        )
      }
      .toList
      .sortBy(_._2)
      .take(2)

    val regrets = insertionCosts.map { case (city, cost) =>
      val regret = insertionCosts
        .filterNot(_._1 == city)
        .map(_._2)
        .max - cost
      (city, regret)
    }

    regrets.maxBy(_._2)
  }
}
