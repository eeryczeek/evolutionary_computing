import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.Assertions

class CostManagerTest extends AnyFunSuite with CostManager with MoveOperations {
  val problemInstance = CSVReader.readCSV("TSPTEST.csv")

  test("additional cost should be correct") {
    val initialSolution = SolutionFactory.getRandomSolution(problemInstance, 0)
    val possibleMoves =
      getAllPossibleMoves(
        initialSolution,
        problemInstance.cities -- initialSolution.path.toSet
      )
    val analyzedMoves = possibleMoves.map { move =>
      val additionalCost = getDeltaCost(problemInstance, move)
      val (newSolution, newAvailableCities) =
        performMove(initialSolution, move, Set.empty)
      (
        move,
        initialSolution.cost + additionalCost,
        Cost.calculateSolutionCost(problemInstance, newSolution)
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
