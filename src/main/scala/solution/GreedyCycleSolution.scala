import scala.annotation.tailrec
object GreedyCycleSolution {
  @tailrec
  def generate(
      problemInstance: ProblemInstance,
      citiesToChooseFrom: Iterable[City],
      currentSolution: PartialSolution
  ): Either[FaultySolution, FullSolution] = {
    if (currentSolution.path.size == problemInstance.expectedSolutionLen) {
      Right(
        FullSolution(
          currentSolution.path,
          cost = GreedyAtAnyPositionSolution.calculatePathLength(
            currentSolution.path,
            problemInstance.distances
          )
        )
      )
    } else {
      val newSolution = insertClosestCityIntoCycle(
        problemInstance.distances,
        citiesToChooseFrom,
        currentSolution
      )
      generate(
        problemInstance,
        citiesToChooseFrom.filterNot(city =>
          newSolution.visitedCities.contains(city)
        ),
        newSolution
      )
    }
  }

  def insertClosestCityIntoCycle(
      distances: Array[Array[Int]],
      citiesToChooseFrom: Iterable[City],
      currentSolution: PartialSolution
  ): PartialSolution = {
    val currentCycle = currentSolution.path
    val (cityToInsertAfter, cityToInsert, additionalCost) = currentCycle
      .zip(
        if (currentCycle.size < 2) currentCycle.tail
        else currentCycle.tail :+ currentCycle.head
      )
      .map(consecutiveCities => {
        val (city1, city2) = consecutiveCities
        val (bestCity, additionalDistance) =
          findBestCityConnectingTwoOthers(
            city1,
            city2,
            distances,
            citiesToChooseFrom
          )
        (city1, bestCity, additionalDistance)
      })
      .minBy(_._3)

    val insertIndex =
      currentSolution.path.zipWithIndex.find(_._1 == cityToInsertAfter).get._2
    val newCycle =
      currentCycle.take(insertIndex) ++
        List(cityToInsert) ++
        currentCycle.drop(insertIndex)

    PartialSolution(
      path = newCycle,
      cost = currentSolution.cost + additionalCost,
      visitedCities = currentSolution.visitedCities + cityToInsert
    )
  }

  def findBestCityConnectingTwoOthers(
      city1: City,
      city2: City,
      distances: Array[Array[Int]],
      citiesToChooseFrom: Iterable[City]
  ): (City, Int) = {
    val city1Id = city1.cityId
    val city2Id = city2.cityId
    citiesToChooseFrom
      .map(middleCity => {
        val middleCityId = middleCity.cityId
        val additionalDistance =
          distances(city1Id)(middleCityId) + distances(middleCityId)(city2Id)
        (middleCity, additionalDistance)
      })
      .minBy(_._2)
  }
}
