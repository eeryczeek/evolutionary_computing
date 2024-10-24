import scala.util.Random
import com.typesafe.scalalogging.Logger

object IntraRouteGreedy extends LocalSearch with SolutionUpdater {
  val logger = Logger("IntraRouteGreedy")
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    if (currentSolution.cost < 0) {
      return (currentSolution, availableCities)
    }
    println("------------------")
    println(currentSolution)
    val possibleMoves =
      getNeighbourhood(problemInstance, currentSolution, availableCities)

    val firstImprovingMove = Random
      .shuffle(possibleMoves)
      .view
      .map(move => (move, getDeltaCost(problemInstance, move)))
      .find { case (move, cost) => cost < 0 }

    firstImprovingMove match {
      case Some((move, deltaCost)) =>
        val (newSolution, newAvailableCities) = updateSolutionWithMove(
          currentSolution,
          move,
          deltaCost,
          availableCities
        )
        println(move)
        println(newSolution)
        (newSolution, newAvailableCities)
      case None =>
        (currentSolution, availableCities)
    }
  }
}
