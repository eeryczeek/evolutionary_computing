import scala.annotation.tailrec

object GreedyTailSolution {
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
