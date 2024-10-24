import scala.annotation.tailrec

object GreedyTailSolution extends MoveOperations with CostManager {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    if (currentSolution.path.size == problemInstance.expectedSolutionLen) {
      val solutionCost = getSolutionCost(problemInstance, currentSolution)
      return (currentSolution.copy(cost = solutionCost), availableCities)
    }

    val allPossibleMoves =
      availableCities.map(city => AppendAtEnd(currentSolution.path.last, city))
    val nextMove = allPossibleMoves
      .minBy(getDeltaCost(problemInstance, _))

    val (newSolution, newAvailableCities) =
      performMove(currentSolution, nextMove, availableCities)
    (newSolution, newAvailableCities)
  }
}
