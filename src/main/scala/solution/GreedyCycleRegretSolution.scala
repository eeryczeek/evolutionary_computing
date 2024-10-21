import scala.annotation.tailrec

object GreedyCycleRegretSolution {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: PartialSolution,
      availableCities: Set[Int]
  ): (PartialSolution, Set[Int]) = {
    val currentCycle = currentSolution.path
    val distances = problemInstance.distances
    val cityCosts = problemInstance.cityCosts
    val edgesWithIndexes = currentCycle
      .zip(currentCycle.tail :+ currentCycle.head)
      .zipWithIndex

    val CityWithPlaceCostAndRegret(
      cityToInsert,
      insertIndex,
      additionalCost,
      _
    ) = availableCities.view
      .flatMap { city =>
        edgesWithIndexes.map { case ((city1, city2), i) =>
          val insertionCost = distances(city1)(city) +
            distances(city)(city2) +
            cityCosts(city) -
            distances(city1)(city2)
          CityWithPlaceAndCost(city, i, insertionCost)
        }
      }
      .groupBy(_.city)
      .mapValues(_.toList.sortBy(_.cost).take(2))
      .map { case (city, costs) =>
        val regret =
          if (costs.size == 2) costs(1).cost - costs(0).cost
          else costs.head.cost
        CityWithPlaceCostAndRegret(
          city,
          costs.head.placeToInsert,
          costs.head.cost,
          regret
        )
      }
      .maxBy(_.regret)

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

  private case class CityWithPlaceAndCost(
      city: Int,
      placeToInsert: Int,
      cost: Int
  )

  private case class CityWithPlaceCostAndRegret(
      city: Int,
      place: Int,
      cost: Int,
      regret: Int
  )
}
