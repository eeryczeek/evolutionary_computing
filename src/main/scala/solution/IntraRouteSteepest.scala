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
    val (bestMove, additionalCost) = possibleMoves
      .map(move =>
        (move, getAdditionalCost(problemInstance, currentSolution, move))
      )
      .minBy { case (move, cost) => cost }

    if (additionalCost < 0) {
      (
        updateSolutionWithMove(
          currentSolution,
          bestMove,
          additionalCost
        ),
        availableCities
      )
    } else {
      (currentSolution, availableCities)
    }
  }
}
