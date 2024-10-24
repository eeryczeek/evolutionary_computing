import scala.util.Random
import scala.util.control.TailCalls.TailRec
import scala.annotation.tailrec

case class Solution(path: List[Int], cost: Int)

object SolutionFactory {
  def getRandomSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    generate(
      problemInstance,
      Solution(
        List.empty,
        cost = problemInstance.cityCosts(initialCity)
      ),
      problemInstance.cities,
      RandomSolution.updateSolution
    )
  }

  def getGreedyAppendSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    generate(
      problemInstance,
      Solution(
        List(initialCity),
        problemInstance.cityCosts(initialCity)
      ),
      problemInstance.cities - initialCity,
      GreedyTailSolution.updateSolution
    )
  }

  def getGreedyAnyPositionSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    generate(
      problemInstance,
      Solution(
        List(initialCity),
        problemInstance.cityCosts(initialCity)
      ),
      problemInstance.cities - initialCity,
      GreedyAtAnyPositionSolution.updateSolution
    )
  }

  def getGreedyCycleSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    generate(
      problemInstance,
      Solution(
        List(initialCity),
        problemInstance.cityCosts(initialCity)
      ),
      problemInstance.cities - initialCity,
      GreedyCycleSolution.updateSolution
    )
  }

  def getGreedyCycleRegretSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    generate(
      problemInstance,
      Solution(
        List(initialCity),
        problemInstance.cityCosts(initialCity)
      ),
      problemInstance.cities - initialCity,
      GreedyCycleRegretSolution.updateSolution
    )
  }

  def getGreedyCycleWeightedRegretSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    generate(
      problemInstance,
      Solution(
        List(initialCity),
        problemInstance.cityCosts(initialCity)
      ),
      problemInstance.cities - initialCity,
      GreedyCycleWeightedRegretSolution.updateSolution
    )
  }

  def getIntraGreedyBasedOnRandomSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val randomSolution = getRandomSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      randomSolution,
      problemInstance.cities -- randomSolution.path,
      IntraRouteGreedy.updateSolution
    )
  }

  def getIntraGreedyBasedOnHeuristicSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val initialSolution =
      getGreedyAnyPositionSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path,
      IntraRouteGreedy.updateSolution
    )
  }

  def getIntraSteepestBasedOnRandomSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val randomSolution = getRandomSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      randomSolution,
      problemInstance.cities -- randomSolution.path,
      IntraRouteSteepest.updateSolution
    )
  }

  def getIntraSteepestBasedOnHeuristicSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val initialSolution =
      getGreedyAnyPositionSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path,
      IntraRouteSteepest.updateSolution
    )
  }

  @tailrec
  def generate(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int],
      updateSolution: (
          ProblemInstance,
          Solution,
          Set[Int]
      ) => (Solution, Set[Int])
  ): Solution = {
    val (newSolution, newAvailableCities) = updateSolution(
      problemInstance,
      currentSolution,
      availableCities
    )

    if (newSolution == currentSolution) {
      Solution(currentSolution.path, currentSolution.cost)
    } else {
      generate(
        problemInstance,
        newSolution,
        newAvailableCities,
        updateSolution
      )
    }
  }

}
