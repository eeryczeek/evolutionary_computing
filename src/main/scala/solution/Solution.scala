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
    generate(
      problemInstance,
      PartialSolution(List.empty, cost = 0),
      problemInstance.cities,
      RandomSolution.updateSolution
    )
  }

  def getGreedyAppendSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): FullSolution = {
    generate(
      problemInstance,
      PartialSolution(List(initialCity), 0),
      problemInstance.cities - initialCity,
      GreedyTailSolution.updateSolution
    )
  }

  def getGreedyAnyPositionSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): FullSolution = {
    generate(
      problemInstance,
      PartialSolution(List(initialCity), 0),
      problemInstance.cities - initialCity,
      GreedyAtAnyPositionSolution.updateSolution
    )
  }

  def getGreedyCycleSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): FullSolution = {
    generate(
      problemInstance,
      PartialSolution(List(initialCity), 0),
      problemInstance.cities - initialCity,
      GreedyCycleSolution.updateSolution
    )
  }

  def getGreedyCycleRegretSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): FullSolution = {
    generate(
      problemInstance,
      PartialSolution(List(initialCity), 0),
      problemInstance.cities - initialCity,
      GreedyCycleRegretSolution.updateSolution
    )
  }

  def getGreedyCycleWeightedRegretSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): FullSolution = {
    generate(
      problemInstance,
      PartialSolution(List(initialCity), 0),
      problemInstance.cities - initialCity,
      GreedyCycleWithWeightedRegret.updateSolution
    )
  }

  @tailrec
  def generate(
      problemInstance: ProblemInstance,
      currentSolution: PartialSolution,
      citiesToChooseFrom: Set[Int],
      updateSolution: (
          ProblemInstance,
          PartialSolution,
          Set[Int]
      ) => (PartialSolution, Set[Int])
  ): FullSolution = {
    if (currentSolution.path.size == problemInstance.expectedSolutionLen) {
      FullSolution(
        currentSolution.path,
        currentSolution.cost + problemInstance.distances(
          currentSolution.path.last
        )(currentSolution.path.head)
      )
    } else {
      val (newPartialSolution, newCitiesToChooseFrom) = updateSolution(
        problemInstance,
        currentSolution,
        citiesToChooseFrom
      )
      generate(
        problemInstance,
        newPartialSolution,
        newCitiesToChooseFrom,
        updateSolution
      )
    }
  }
}
