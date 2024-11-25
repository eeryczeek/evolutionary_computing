import scala.annotation.tailrec
import scala.util.Random

object RandomSolution extends CostManager {
  def updateSolution(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    if (
      currentSolution.path.size == ProblemInstanceHolder.problemInstance.expectedSolutionLen
    ) {
      val solutionCost = getSolutionCost(currentSolution)
      return (currentSolution.copy(cost = solutionCost), availableCities)
    }
    val solutionPath = Random
      .shuffle(availableCities.toSeq)
      .take(ProblemInstanceHolder.problemInstance.expectedSolutionLen)
      .toArray

    (
      Solution(solutionPath, 0),
      availableCities -- solutionPath
    )
  }
}
