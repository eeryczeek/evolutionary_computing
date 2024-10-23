import com.typesafe.scalalogging.Logger
import scala.util.Random
object IntraRouteSteepest extends IntraRoute with SolutionUpdater {
  val logger = Logger("IntraRouteSteepest")
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val possibleMoves = findPossibleMoves(currentSolution)
    logger.debug(s"Number of possible moves: ${possibleMoves.size}")
    val evaluatedMoves = possibleMoves
      .map(move =>
        (move, getAdditionalCost(problemInstance, currentSolution, move))
      )
      .sortBy { case (move, cost) => cost }

    logger.debug(s"Best moves: ${evaluatedMoves.take(5)}")
    logger.debug(
      s"Best node swaps: ${evaluatedMoves.filter(_._1.isInstanceOf[NodeSwap]).take(5)}"
    )

    val (bestMove, additionalCost) = evaluatedMoves.minBy { case (move, cost) =>
      cost
    }

    logger.debug(s"Additional cost: $additionalCost")
    logger.debug(s"New cost: ${currentSolution.cost + additionalCost}")
    if (additionalCost < 0) {
      (
        updateSolutionWithMove(currentSolution, bestMove, additionalCost),
        availableCities
      )
    } else {
      (currentSolution, availableCities)
    }
  }
}
