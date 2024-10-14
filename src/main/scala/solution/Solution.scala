import scala.util.Random
import scala.util.control.TailCalls.TailRec
import scala.annotation.tailrec

trait Solution
case class PartialSolution(path: List[Int], cost: Int) extends Solution
case class FullSolution(path: List[Int], cost: Int) extends Solution

object SolutionFactory {
  def getRandomSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): FullSolution = {
    RandomSolution.generate(
      problemInstance,
      PartialSolution(List.empty, cost = 0),
      problemInstance.cities
    )
  }

  def getGreedyAppendSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): FullSolution = {
    GreedyTailSolution.generate(
      problemInstance,
      PartialSolution(List(initialCity), 0),
      problemInstance.cities - initialCity
    )
  }

  def getGreedyAnyPositionSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): FullSolution = {
    GreedyAtAnyPositionSolution.generate(
      problemInstance,
      PartialSolution(List(initialCity), 0),
      problemInstance.cities - initialCity
    )
  }

  def getGreedyCycleSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): FullSolution = {
    GreedyCycleSolution.generate(
      problemInstance,
      PartialSolution(List(initialCity), 0),
      problemInstance.cities - initialCity
    )
  }
}
