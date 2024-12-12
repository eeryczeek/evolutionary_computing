import scala.util.Random
import scala.annotation.tailrec

object LocalSearchGreedy
    extends MoveGenerator
    with MoveOperations
    with CostManager {

  @tailrec
  def modifySolution(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val iterations = currentSolution.additionalData
      .getOrElse(AdditionalData(Some(0)))
      .numOfIterations
      .getOrElse(0)
    val firstImprovingMove =
      getFirstImprovingMoveFromEdgeSwapNeighborhood(
        currentSolution,
        availableCities
      )

    firstImprovingMove match {
      case Some(move) =>
        val deltaCost = getDeltaCost(move)
        val (newSolution, newAvailableCities) =
          performMove(currentSolution, move, availableCities)
        modifySolution(
          newSolution.copy(
            cost = currentSolution.cost + deltaCost,
            additionalData = Some(AdditionalData(Some(iterations + 1)))
          ),
          newAvailableCities
        )
      case None =>
        (currentSolution, availableCities)
    }
  }
}
