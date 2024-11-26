import scala.annotation.tailrec

object GreedyCycleRegretSolution
    extends MoveOperations
    with CostManager
    with LocalSearch {
  def updateSolution(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val move = getAllInsertBetween(currentSolution, availableCities)
      .map(move => (move, getDeltaCost(move)))
      .groupBy(_._1.city)
      .mapValues(_.toList.sortBy(_._2).take(2))
      .map { case (city, moves) =>
        val regret =
          if (moves.size == 2) moves(1)._2 - moves(0)._2
          else moves.head._2
        (city, moves.head._1, regret)
      }
      .maxBy(_._3)
      ._2

    val (newSolution, newAvailableCities) =
      performMove(currentSolution, move, availableCities)
    (newSolution, newAvailableCities)
  }

}
