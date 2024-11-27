import scala.util.Random
import scala.annotation.tailrec

object LocalSearchGreedy
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

    val firstImprovingMove = Random
      .shuffle(possibleMoves)
      .find(move => getDeltaCost(move) < 0)

    firstImprovingMove match {
      case Some(move) =>
        val deltaCost = getDeltaCost(move)
        val (newSolution, newAvailableCities) =
          performMove(currentSolution, move, availableCities)
        modifySolution(
          newSolution.copy(cost = currentSolution.cost + deltaCost),
          newAvailableCities,
          neighbourhoodGenerator
        )
      case None =>
        (currentSolution, availableCities)
    }
  }
}
