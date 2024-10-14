import scala.annotation.tailrec

object GreedyTailSolution {
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
      val (newSolution, newAvailableCities) =
        updateSolution(problemInstance, currentSolution, availableCities)
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
    val nextCity = availableCities
      .minBy(cityId =>
        problemInstance.distances(currentSolution.path.last)(cityId)
      )
    (
      PartialSolution(
        currentSolution.path :+ nextCity,
        currentSolution.cost + problemInstance.distances(
          currentSolution.path.last
        )(nextCity)
      ),
      availableCities - nextCity
    )
  }
}
