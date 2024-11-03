import scala.util.Random

object LocalSearchWithCandidateMovesSteepest
    extends LocalSearch
    with MoveOperations
    with CostManager {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val possibleMoves =
      getCandidateMoves(
        problemInstance,
        currentSolution,
        availableCities
      )

    val bestImprovingMove = possibleMoves
      .map(move => (move, getDeltaCost(problemInstance, move)))
      .minBy { case (_, cost) => cost }

    bestImprovingMove match {
      case (move, deltaCost) if deltaCost < 0 => {
        val (newSolution, newAvailableCities) = performMove(
          currentSolution,
          move,
          availableCities
        )
        (
          newSolution.copy(cost = currentSolution.cost + deltaCost),
          newAvailableCities
        )
      }
      case _ =>
        (currentSolution, availableCities)
    }
  }
}
