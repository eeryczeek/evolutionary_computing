import scala.annotation.tailrec
object GreedyCycleSolution {
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
          cost = GreedyAtAnyPositionSolution.calculatePathLength(
            currentSolution.path,
            problemInstance.distances
          )
        )
      )
    } else {
      val newPath = insertClosestCityIntoCycle(
        problemInstance.distances,
        citiesToChooseFrom,
        currentSolution.path
      )
      generate(
        problemInstance,
        citiesToChooseFrom.filterNot(city =>
          newPath.exists(_.cityId == city.cityId)
        ),
        PartrialSolution(
          newPath,
          currentSolution.visitedCities ++ newPath,
          GreedyAtAnyPositionSolution.calculatePathLength(
            newPath,
            problemInstance.distances
          )
        )
      )
    }
  }

  def insertClosestCityIntoCycle(
      distances: Array[Array[Int]],
      citiesToChooseFrom: Iterable[City],
      currentCycle: List[City]
  ): List[City] = ???

}
