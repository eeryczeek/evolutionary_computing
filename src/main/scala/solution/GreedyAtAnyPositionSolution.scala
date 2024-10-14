import scala.annotation.tailrec

object GreedyAtAnyPositionSolution {
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
          calculatePathLength(currentSolution.path, problemInstance.distances)
        )
      )
    } else {
      val newPath = insertCityIntoPath2(
        currentSolution.path,
        problemInstance.distances,
        citiesToChooseFrom
      )
      generate(
        problemInstance,
        citiesToChooseFrom.filterNot(city => newPath.contains(city)),
        PartialSolution(
          newPath,
          calculatePathLength(newPath, problemInstance.distances)
        )
      )
    }
  }

  def findClosestCity(
      city: City,
      distances: Array[Array[Int]],
      citiesToChooseFrom: Iterable[City]
  ): City = {
    citiesToChooseFrom.minBy(c => distances(city.id)(c.id))
  }

  def insertClosestCityIntoPath(
      path: List[City],
      distances: Array[Array[Int]],
      citiesToChooseFrom: Iterable[City]
  ): List[City] = {
    val (positionToInsertAfter, closestCity) = path.zipWithIndex
      .map { case (city, idx) =>
        (idx, findClosestCity(city, distances, citiesToChooseFrom))
      }
      .minBy { case (idx, city) => distances(path(idx).id)(city.id) }

    path.take(positionToInsertAfter + 1) ++ List(closestCity) ++ path.drop(
      positionToInsertAfter + 1
    )
  }

  def calculatePathLength(
      path: Iterable[City],
      distances: Array[Array[Int]]
  ): Int = {
    if (path.isEmpty) 0
    else {
      val cityList = path.toList
      cityList
        .zip(cityList.tail :+ cityList.head)
        .map { case (city1, city2) => distances(city1.id)(city2.id) }
        .sum
    }
  }

  def additionalCostOfInsertingCity(
      city1: City,
      city2: City,
      cityToInsert: City,
      distances: Array[Array[Int]]
  ): Int = {
    distances(city1.id)(cityToInsert.id) + distances(cityToInsert.id)(
      city2.id
    ) - distances(city1.id)(city2.id)
  }

  def insertCityIntoPath2(
      path: List[City],
      distances: Array[Array[Int]],
      citiesToChooseFrom: Iterable[City]
  ): List[City] = {
    val (bestCityToPrepend, additionalCostPrepend) = citiesToChooseFrom
      .map { cityToInsert =>
        (cityToInsert, distances(cityToInsert.id)(path.head.id))
      }
      .minBy { case (city, additionalCost) => additionalCost }

    val citiesWithAdditionalCosts = path
      .zip(path.tail)
      .map { case (city1, city2) =>
        val (bestToInsert, additionalCost) = citiesToChooseFrom
          .map(city =>
            (city, additionalCostOfInsertingCity(city1, city2, city, distances))
          )
          .minBy { case (city, additionalCost) => additionalCost }
        (path.indexOf(city1), bestToInsert, additionalCost)
      }

    val (indexToInsertAt, bestCityToInsert, additionalCostInsert) =
      if (citiesWithAdditionalCosts.isEmpty) (0, path.head, 1_000_000)
      else
        citiesWithAdditionalCosts
          .minBy { case (city1, cityToInsert, additionalCost) =>
            additionalCost
          }

    val (bestCityToAppend, additionalCostAppend) = citiesToChooseFrom
      .map { cityToAppend =>
        (cityToAppend, distances(path.last.id)(cityToAppend.id))
      }
      .minBy { case (city, additionalCost) => additionalCost }

    if (
      additionalCostPrepend <= additionalCostInsert && additionalCostPrepend <= additionalCostAppend
    ) {
      bestCityToPrepend :: path
    } else if (
      additionalCostInsert <= additionalCostPrepend && additionalCostInsert <= additionalCostAppend
    ) {
      path.take(indexToInsertAt + 1) ++ List(bestCityToInsert) ++ path.drop(
        indexToInsertAt + 1
      )
    } else {
      path :+ bestCityToAppend
    }
  }
}
