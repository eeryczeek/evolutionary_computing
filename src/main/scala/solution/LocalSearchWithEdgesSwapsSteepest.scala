import scala.util.Random

object LocalSearchWithEdgesSwapsSteepest
    extends LocalSearch
    with MoveOperations
    with CostManager {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val possibleMoves =
      getNeighbourhoodWithEdgesSwapsIn(
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
        (newSolution, newAvailableCities)
      }
      case _ =>
        (currentSolution, availableCities)
    }
  }
}
