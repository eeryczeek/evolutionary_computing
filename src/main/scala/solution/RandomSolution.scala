import scala.annotation.tailrec
import scala.util.Random

object RandomSolution extends CostManager {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    if (currentSolution.path.size == problemInstance.expectedSolutionLen) {
      val solutionCost = getSolutionCost(problemInstance, currentSolution)
      return (currentSolution.copy(cost = solutionCost), availableCities)
    }
    Random.setSeed(System.currentTimeMillis())
    val solutionPath = Random
      .shuffle(availableCities)
      .take(problemInstance.expectedSolutionLen)
      .toArray

    (
      Solution(solutionPath, 0),
      availableCities -- solutionPath
    )
  }
}
