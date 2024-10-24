import com.typesafe.scalalogging.Logger
import scala.util.Random

object IntraRouteSteepest extends LocalSearch with SolutionUpdater {
  val logger = Logger("IntraRouteSteepest")
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val possibleMoves =
      getNeighbourhood(problemInstance, currentSolution, availableCities)
    val (bestMove, deltaCost) = possibleMoves
      .map(move => (move, getDeltaCost(problemInstance, move)))
      .minBy { case (move, cost) => cost }

    if (deltaCost < 0) {

      val (newSolution, newAvailableCities) = updateSolutionWithMove(
        currentSolution,
        bestMove,
        deltaCost,
        availableCities
      )
      (newSolution, newAvailableCities)

    } else {
      (currentSolution, availableCities)
    }
  }
}
