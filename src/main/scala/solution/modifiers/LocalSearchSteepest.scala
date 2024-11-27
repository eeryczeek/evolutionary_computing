import scala.util.Random
import scala.annotation.tailrec

object LocalSearchSteepest
    extends MoveGenerator
    with MoveOperations
    with CostManager {

  @tailrec
  def modifySolution(
      currentSolution: Solution,
      availableCities: Set[Int],
      neighbourhoodGenerator: (Solution, Set[Int]) => Seq[Move]
  ): (Solution, Set[Int]) = {
    val possibleMoves = neighbourhoodGenerator(currentSolution, availableCities)

    val improvingMoves = possibleMoves
      .map(move => (move, getDeltaCost(move)))
      .minBy { case (_, cost) => cost }

    if (improvingMoves._2 < 0) {
      val (bestMove, deltaCost) = improvingMoves
      val (newSolution, newAvailableCities) =
        performMove(currentSolution, bestMove, availableCities)
      modifySolution(
        newSolution.copy(cost = currentSolution.cost + deltaCost),
        newAvailableCities,
        neighbourhoodGenerator
      )
    } else {
      (currentSolution, availableCities)
    }
  }
}
