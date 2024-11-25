import scala.util.Random

object LocalSearchWithEdgesSwapsSteepest
    extends LocalSearch
    with MoveOperations
    with CostManager {
  def updateSolution(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val possibleMoves = getAllEdgeSwaps(currentSolution, availableCities) ++
      getAllNodeSwapsOut(currentSolution, availableCities)

    val bestImprovingMove = possibleMoves
      .map(move => (move, getDeltaCost(move)))
      .minBy { case (_, cost) => cost }

    bestImprovingMove match {
      case (move, deltaCost) if deltaCost < 0 => {
        val (newSolution, newAvailableCities) =
          performMove(currentSolution, move, availableCities)
        (
          newSolution.copy(cost = currentSolution.cost + deltaCost),
          newAvailableCities
        )
      }
      case _ => (currentSolution, availableCities)
    }
  }
}
