import scala.annotation.tailrec

object GreedyTailSolution
    extends MoveOperations
    with CostManager
    with LocalSearch {
  def updateSolution(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val possibleMoves = getAllTailAppends(currentSolution, availableCities)
    val (move, deltaCost) =
      possibleMoves.map(move => (move, getDeltaCost(move))).minBy(_._2)
    val (newSolution, newAvailableCities) =
      performMove(currentSolution, move, availableCities)
    (newSolution, newAvailableCities)
  }
}
