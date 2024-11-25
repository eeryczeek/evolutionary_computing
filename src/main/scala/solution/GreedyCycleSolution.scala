object GreedyCycleSolution extends MoveOperations with CostManager {

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

    val pairs = getConsecutivePairs(currentSolution.path)
    val nextMove = availableCities
      .flatMap { city =>
        pairs.map { pair =>
          InsertBetween(pair, city)
        }
      }
      .minBy(getDeltaCost(_))

    val (newSolution, newAvailableCities) =
      performMove(currentSolution, nextMove, availableCities)

    (newSolution, newAvailableCities)
  }
}
