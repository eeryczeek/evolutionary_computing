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
        citiesToChooseFrom.filterNot(city => newSolution.path.contains(city)),
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
        currentCycle.size match {
          case 1 => List(currentCycle.head)
          case _ => currentCycle.tail :+ currentCycle.head
        }
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

    val insertIndex = currentSolution.path.indexOf(cityToInsertAfter)
    val newCycle = currentCycle.take(insertIndex + 1) ++ List(
      cityToInsert
    ) ++ currentCycle.drop(insertIndex + 1)

    PartialSolution(
      path = newCycle,
      cost = currentSolution.cost + additionalCost
    )
  }

  def findBestCityConnectingTwoOthers(
      city1: City,
      city2: City,
      distances: Array[Array[Int]],
      citiesToChooseFrom: Iterable[City]
  ): (City, Int) = {
    citiesToChooseFrom
      .map(middleCity => {
        (
          middleCity,
          distances(city1.id)(middleCity.id)
            + distances(middleCity.id)(city2.id)
            - distances(city1.id)(city2.id)
        )
      })
      .minBy(_._2)
  }
}
