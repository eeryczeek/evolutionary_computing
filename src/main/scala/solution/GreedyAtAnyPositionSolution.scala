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
      val newPath = insertClosestCityIntoPath(
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
}
