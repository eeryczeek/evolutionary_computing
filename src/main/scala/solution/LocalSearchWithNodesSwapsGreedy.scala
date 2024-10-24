import scala.util.Random

object LocalSearchWithNodesSwapsGreedy
    extends LocalSearch
    with MoveOperations
    with CostManager {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val possibleMoves =
      getNeighbourhoodWithNodesSwapsIn(
        problemInstance,
        currentSolution,
        availableCities
      )

    val firstImprovingMove = Random
      .shuffle(possibleMoves)
      .find(move => getDeltaCost(problemInstance, move) < 0)

    firstImprovingMove match {
      case Some(move) => {
        val deltaCost = getDeltaCost(problemInstance, move)
        val (newSolution, newAvailableCities) = performMove(
          currentSolution,
          move,
          availableCities
        )
        (newSolution, newAvailableCities)
      }
      case None =>
        (currentSolution, availableCities)
    }
  }
}
