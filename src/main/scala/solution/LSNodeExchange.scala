import scala.util.Random

object LocalSearchWithNodeExchange
    extends LocalSearch
    with MoveOperations
    with CostManager {
  def updateSolution(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val possibleMoves =
      getNeighbourhoodWithEdgesSwapsIn(
        currentSolution,
        availableCities
      )

    val firstImprovingMove = Random
      .shuffle(possibleMoves)
      .find { move => getDeltaCost(move) < 0 }

    firstImprovingMove match {
      case Some(move) => {
        val deltaCost = getDeltaCost(move)
        val (newSolution, newAvailableCities) =
          performMove(currentSolution, move, availableCities)
        (
          newSolution.copy(cost = currentSolution.cost + deltaCost),
          newAvailableCities
        )
      }
      case None =>
        (currentSolution, availableCities)
    }
  }
}
