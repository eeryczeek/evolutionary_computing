object CycleGenerator
    extends MoveOperations
    with CostManager
    with MoveGenerator {
  def updateSolution(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val possibleMoves = getAllInsertBetween(currentSolution, availableCities)
    val move = possibleMoves.minBy(getDeltaCost(_))
    val (newSolution, newAvailableCities) =
      performMove(currentSolution, move, availableCities)
    (newSolution, newAvailableCities)
  }
}
