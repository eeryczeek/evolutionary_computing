import scala.annotation.tailrec

object GreedyTailSolution {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: PartialSolution,
      availableCities: Set[Int]
  ): (PartialSolution, Set[Int]) = {
    val nextCity = availableCities
      .minBy(cityId =>
        problemInstance.distances(currentSolution.path.last)(
          cityId
        ) + problemInstance.cityCosts(cityId)
      )
    val head = currentSolution.path.head
    val last = currentSolution.path.last
    (
      PartialSolution(
        currentSolution.path :+ nextCity,
        currentSolution.cost +
          problemInstance.distances(last)(nextCity) +
          problemInstance.cityCosts(nextCity) +
          problemInstance.distances(nextCity)(head) -
          problemInstance.distances(last)(head)
      ),
      availableCities - nextCity
    )
  }
}
