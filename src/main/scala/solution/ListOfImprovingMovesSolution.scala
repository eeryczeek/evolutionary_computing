import scala.collection.mutable
import scala.util.Random

class ListOfImprovingMovesSolution(problemInstance: ProblemInstance)
    extends LocalSearch
    with MoveOperations
    with CostManager {

  private val improvingMoves = mutable.PriorityQueue[(Move, Int)]()(
    Ordering.by((_: (Move, Int))._2).reverse
  )

  def init(initialSolution: Solution, availableCities: Set[Int]): Unit = {
    val edgeSwaps =
      getAllEdgeSwaps(problemInstance, currentSolution, availableCities)
    val nodeSwapsOut =
      getAllNodeSwapsOut(problemInstance, currentSolution, availableCities)
    val possibleMoves = edgeSwaps ++ nodeSwapsOut

    val improvingMovesWithCosts = possibleMoves
      .map(move => (move, getDeltaCost(problemInstance, move)))
      .filter(_._2 < 0)

    improvingMoves.enqueue(improvingMovesWithCosts: _*)
  }

  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    improvingMoves.dequeueOption match {
      case Some((move, _)) =>
        val (newSolution, newAvailableCities) =
          performMove(currentSolution, move, availableCities)

        updateImprovingMoves(
          problemInstance,
          newSolution,
          newAvailableCities,
          move
        )

        (newSolution, newAvailableCities)
      case None =>
        (currentSolution, availableCities)
    }
  }

  def updateImprovingMoves(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int],
      move: Move
  ): Unit = {
    move match {
      case edgeSwap: EdgeSwap =>
        val newEdgeSwaps =
          getAllEdgeSwaps(problemInstance, currentSolution, availableCities)
        val newImprovingMoves = newEdgeSwaps
          .map(move => (move, getDeltaCost(problemInstance, move)))
          .filter(_._2 < 0)

        improvingMoves.enqueue(newImprovingMoves: _*)
      case nodeSwapOut: NodeSwapOut =>
        val newNodeSwapsOut =
          getAllNodeSwapsOut(problemInstance, currentSolution, availableCities)
        val newImprovingMoves = newNodeSwapsOut
          .map(move => (move, getDeltaCost(problemInstance, move)))
          .filter(_._2 < 0)

        improvingMoves.enqueue(newImprovingMoves: _*)
    }
  }
}
