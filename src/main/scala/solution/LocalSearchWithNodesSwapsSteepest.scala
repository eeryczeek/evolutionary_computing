import scala.util.Random

object LocalSearchWithNodesSwapsSteepest
    extends LocalSearch
    with MoveOperations
    with CostManager {
  def updateSolution(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val possibleMoves =
      getNeighbourhoodWithNodesSwapsIn(currentSolution, availableCities)

    val improvingMoves = possibleMoves
      .map(move => (move, getDeltaCost(move)))
      .minBy { case (_, cost) => cost }

    if (improvingMoves._2 < 0) {
      val (bestMove, deltaCost) = improvingMoves
      val (newSolution, newAvailableCities) =
        performMove(currentSolution, bestMove, availableCities)
      (newSolution, newAvailableCities)
    } else {
      (currentSolution, availableCities)
    }
  }
}
