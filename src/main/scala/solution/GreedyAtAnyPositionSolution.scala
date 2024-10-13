import scala.annotation.tailrec
object GreedyAtAnyPositionSolution {
  @tailrec
  def generate(
      problemInstance: ProblemInstance,
      citiesToChooseFrom: Iterable[City],
      currentSolution: PartrialSolution
  ): Either[FaultySolution, FullSolution] = {
    if (currentSolution.path.size == problemInstance.expectedSolutionLen) {
      Right(
        FullSolution(
          currentSolution.path,
          cost = calculatePathLength(
            currentSolution.path,
            problemInstance.distances
          )
        )
      )
    } else {
      val newPath = insertClosestCityIntoPath(
        currentSolution.path,
        problemInstance.distances,
        citiesToChooseFrom
      )
      generate(
        problemInstance,
        citiesToChooseFrom.filterNot(city =>
          newPath.exists(_.cityId == city.cityId)
        ),
        PartrialSolution(
          newPath,
          currentSolution.visitedCities ++ newPath,
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
    val closestCityId = distances(city.cityId).zipWithIndex
      .filter { case (_, cityId) =>
        citiesToChooseFrom.exists(_.cityId == cityId)
      }
      .minBy(_._1)
      ._2

    citiesToChooseFrom.find(_.cityId == closestCityId).get
  }

  def insertClosestCityIntoPath(
      path: List[City],
      distances: Array[Array[Int]],
      citiesToChooseFrom: Iterable[City]
  ): List[City] = {
    // find the city which is closest to on of the cities in the path and insert it in the path
    val closestCityAtBeginningId = distances
      .map(distancesFromCities => distancesFromCities(path.head.cityId))
      .zipWithIndex
      .filter { case (_, cityId) =>
        citiesToChooseFrom.exists(_.cityId == cityId)
      }
      .minBy(_._1)
      ._2

    val closestCityAtBeginning =
      citiesToChooseFrom.find(_.cityId == closestCityAtBeginningId).get
    val distanceFromClosestCityAtBeginning =
      distances(closestCityAtBeginning.cityId)(path.head.cityId)

    val (positionToInsertAfter, closestCityInPath, distanceToCityInPath) =
      path.zipWithIndex
        .map(cityWithId =>
          (
            cityWithId._2,
            findClosestCity(cityWithId._1, distances, citiesToChooseFrom)
          )
        )
        .map(positionWithCity => {
          val (position, city) = positionWithCity
          val distanceFromPreviousCity =
            distances(path(position).cityId)(city.cityId)
          (position, city, distanceFromPreviousCity)
        })
        .minBy(_._3)
    if (distanceFromClosestCityAtBeginning < distanceToCityInPath) {
      List(closestCityAtBeginning) ++ path
    } else {
      path.take(positionToInsertAfter) ++
        List(closestCityInPath) ++
        path.drop(positionToInsertAfter)
    }
  }

  def calculatePathLength(
      path: Iterable[City],
      distances: Array[Array[Int]]
  ): Int = {
    path
      .zip(path.tail)
      .map { case (city1, city2) =>
        distances(city1.cityId)(city2.cityId)
      }
      .sum + distances(path.head.cityId)(path.last.cityId)
  }
}
