import scala.annotation.tailrec

object GreedyTailSolution extends MoveOperations with CostManager {
  def updateSolution(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    if (
      currentSolution.path.size == ProblemInstanceHolder.problemInstance.expectedSolutionLen
    ) {
      val solutionCost = getSolutionCost(currentSolution)
      return (currentSolution.copy(cost = solutionCost), availableCities)
    }

    val allPossibleMoves =
      availableCities.map(city => AppendAtEnd(currentSolution.path.last, city))
    val nextMove = allPossibleMoves.minBy(getDeltaCost(_))

    val (newSolution, newAvailableCities) =
      performMove(currentSolution, nextMove, availableCities)
    (newSolution, newAvailableCities)
  }
}
