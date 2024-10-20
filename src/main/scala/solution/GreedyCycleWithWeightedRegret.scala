object GreedyCycleWithWeightedRegret {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: PartialSolution,
      availableCities: Set[Int]
  ): (PartialSolution, Set[Int]) = {
    val currentCycle = currentSolution.path
    val distances = problemInstance.distances

    val CityWithPlaceCostAndRegret(
      cityToInsert,
      insertIndex,
      additionalCost,
      _
    ) = availableCities.view
      .flatMap { city =>
        currentCycle
          .zip(currentCycle.tail :+ currentCycle.head)
          .zipWithIndex
          .map { case ((city1, city2), i) =>
            val insertionCost = distances(city1)(city) + distances(city)(
              city2
            ) - distances(city1)(city2)
            CityWithPlaceAndCost(city, i, insertionCost)
          }
      }
      .groupBy(_.city)
      .mapValues(_.toList.sortBy(_.cost).take(2))
      .map { case (city, costs) =>
        val regret =
          if (costs.size == 2)
            weightedRegret(costs.head.cost, costs.last.cost, 0.5)
          else costs.head.cost
        CityWithPlaceCostAndRegret(
          city,
          costs.head.placeToInsert,
          costs.head.cost,
          regret
        )
      }
      .minBy(_.weightedRegret)

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

  private def weightedRegret(
      firstScore: Int,
      secondScore: Int,
      weight: Double
  ): Double = weight * secondScore + (1d - 2 * weight) * firstScore

  private case class CityWithPlaceAndCost(
      city: Int,
      placeToInsert: Int,
      cost: Int
  )

  private case class CityWithPlaceCostAndRegret(
      city: Int,
      place: Int,
      cost: Int,
      weightedRegret: Double
  )
}
