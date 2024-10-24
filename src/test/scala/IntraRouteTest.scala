import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.Assertions
class IntraRouteTest extends AnyFunSuite with IntraRoute {
  val problemInstance = CSVReader.readCSV("TSPA.csv")
  test("additional cost should be correct") {
    val initialSolution =
      SolutionFactory.getRandomSolution(problemInstance, 0)
    val possibleMoves = findPossibleMoves(initialSolution)
    val analyzedMoves = possibleMoves.map { move =>
      val additionalCost =
        getAdditionalCost(problemInstance, initialSolution, move)
      val updatedSolution = updateSolutionWithMove(
        initialSolution,
        move,
        additionalCost
      )
      (
        move,
        updatedSolution.cost,
        Cost.calculateSolutionCost(
          problemInstance,
          updatedSolution
        )
      )
    }

    analyzedMoves.foreach { case (move, calculatedCost, trueCost) =>
      if (calculatedCost != trueCost)
        println(
          s"for move: $move -> cost is: $calculatedCost, should be ${trueCost}"
        )
    }

    Assertions.assert(analyzedMoves.forall {
      case (_, calculatedCost, actualCost) => calculatedCost == actualCost
    })
  }

  test("updates correctly for node swap") {
    val initialSolution = Solution(path = (0 to 5).toList, cost = 6)
    val swap = NodeSwap(1, 1, 4, 4)

    val updatedSolution =
      updateSolutionWithMove(initialSolution, swap, 0)

    assert(updatedSolution.path == List(0, 4, 2, 3, 1, 5))
  }

  test("correctly calculates additional cost for node swaps") {
    val swap = NodeSwap(1, 1, 4, 4)
    val initialSolution = Solution((0 to 5).toList, 0)
    val actualCostOfSolution =
      Cost.calculateSolutionCost(problemInstance, initialSolution)
    val additionalCost =
      getAdditionalCost(problemInstance, initialSolution, swap)
    val uptadatedCycle = updateSolutionWithMove(
      initialSolution.copy(cost = actualCostOfSolution),
      swap,
      additionalCost
    )

    val expectedCost = Cost.calculateSolutionCost(
      problemInstance,
      Solution(List(0, 4, 2, 3, 1, 5), 0)
    )

    assert(uptadatedCycle.cost == expectedCost)
  }
}
