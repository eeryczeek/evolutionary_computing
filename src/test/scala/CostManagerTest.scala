import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.Assertions

class CostManagerTest
    extends AnyFunSuite
    with CostManager
    with MoveGenerator
    with MoveOperations {

  test("additional cost should be correct") {
    ProblemInstanceHolder.problemInstance = CSVReader.readCSV(s"TSPTEST.csv")
    val initialSolution = SolutionFactory.getRandomSolution()

    val possibleMoves =
      getAllEdgeSwaps(
        initialSolution,
        ProblemInstanceHolder.problemInstance.cities -- initialSolution.path
      ) ++
        getAllNodeSwapsOut(
          initialSolution,
          ProblemInstanceHolder.problemInstance.cities -- initialSolution.path
        ) ++
        getAllNodeSwapsIn(
          initialSolution,
          ProblemInstanceHolder.problemInstance.cities -- initialSolution.path
        ) ++
        getAllTwoNodeExchange(
          initialSolution,
          ProblemInstanceHolder.problemInstance.cities -- initialSolution.path
        )

    val analyzedMoves = possibleMoves.map { move =>
      val additionalCost = getDeltaCost(move)
      val (newSolution, newAvailableCities) =
        performMove(initialSolution, move, Set.empty)
      (
        move,
        initialSolution.cost + additionalCost,
        Cost.calculateSolutionCost(newSolution)
      )
    }

    analyzedMoves.foreach { case (move, calculatedCost, trueCost) =>
      if (calculatedCost != trueCost)
        println(
          s"for move: $move -> cost is: $calculatedCost, should be $trueCost"
        )
    }

    Assertions.assert(analyzedMoves.forall {
      case (_, calculatedCost, actualCost) => calculatedCost == actualCost
    })
  }
}
