import scala.util.Random
import com.typesafe.scalalogging.Logger

object IntraRouteGreedy extends IntraRoute with SolutionUpdater {
  val logger = Logger("IntraRouteGreedy")
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val possibleMoves = findPossibleMoves(currentSolution)

    val firstImprovingMove = Random
      .shuffle(possibleMoves)
      .view
      .map(move =>
        (move, getAdditionalCost(problemInstance, currentSolution, move))
      )
      .find { case (move, cost) => cost < 0 }

    firstImprovingMove match {
      case Some((move, additionalCost)) =>
        (
          updateSolutionWithMove(
            currentSolution,
            move,
            additionalCost
          ),
          availableCities
        )
      case None =>
        (currentSolution, availableCities)
    }
  }
}