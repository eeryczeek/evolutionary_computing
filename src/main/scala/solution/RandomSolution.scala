import scala.annotation.tailrec
import scala.util.Random

object RandomSolution extends CostManager {
  def generate(): Solution = {
    val solutionPath = Random
      .shuffle(ProblemInstanceHolder.problemInstance.cities.toSeq)
      .take(ProblemInstanceHolder.problemInstance.expectedSolutionLen)
      .toSeq
    Solution(solutionPath, calculateSolutionCost(solutionPath))
  }
}
