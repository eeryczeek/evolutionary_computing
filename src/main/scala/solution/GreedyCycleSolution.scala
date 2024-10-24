object GreedyCycleSolution extends MoveOperations with CostManager {

  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    if (currentSolution.path.size == problemInstance.expectedSolutionLen) {
      val solutionCost = getSolutionCost(problemInstance, currentSolution)
      return (currentSolution.copy(cost = solutionCost), availableCities)
    }

    val pairs = getConsecutivePairs(currentSolution)
    val nextMove = availableCities
      .flatMap { city =>
        pairs.map { pair =>
          InsertBetween(pair, city)
        }
      }
      .minBy(getDeltaCost(problemInstance, _))

    val (newSolution, newAvailableCities) =
      performMove(currentSolution, nextMove, availableCities)

    (newSolution, newAvailableCities)
  }
}
