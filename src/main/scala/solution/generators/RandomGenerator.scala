import scala.annotation.tailrec
import scala.util.Random

object RandomGenerator extends CostManager {
  def generate(): Solution = {
    val solutionPath = Random
      .shuffle(ProblemInstanceHolder.problemInstance.cities.toSeq)
      .take(ProblemInstanceHolder.problemInstance.expectedSolutionLen)
      .toSeq
    Solution(solutionPath, calculateSolutionCost(solutionPath))
  }
}
