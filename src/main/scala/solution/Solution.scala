import scala.util.Random
import scala.util.control.TailCalls.TailRec
import scala.annotation.tailrec

case class Solution(path: Array[Int], cost: Int)

object SolutionFactory {
  def getRandomSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    generate(
      problemInstance,
      Solution(Array.empty, 0),
      problemInstance.cities,
      RandomSolution.updateSolution
    )
  }

  def getGreedyTailSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    generate(
      problemInstance,
      Solution(
        Array(initialCity),
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
        Array(initialCity),
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
        Array(initialCity),
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
        Array(initialCity),
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
        Array(initialCity),
        problemInstance.cityCosts(initialCity)
      ),
      problemInstance.cities - initialCity,
      GreedyCycleWeightedRegretSolution.updateSolution
    )
  }

  def getLocalSearchWithEdgesSwapsGreedyRandomStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val randomSolution = getRandomSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      randomSolution,
      problemInstance.cities -- randomSolution.path,
      LocalSearchWithEdgesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithEdgesSwapsGreedyHeuristicStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val initialSolution =
      getGreedyAnyPositionSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path,
      LocalSearchWithEdgesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithEdgesSwapsSteepestRandomStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val randomSolution = getRandomSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      randomSolution,
      problemInstance.cities -- randomSolution.path,
      LocalSearchWithEdgesSwapsSteepest.updateSolution
    )
  }

  def getLocalSearchWithEdgesSwapsSteepestHeuristicStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val initialSolution =
      getGreedyAnyPositionSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path,
      LocalSearchWithEdgesSwapsSteepest.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsGreedyRandomStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val randomSolution = getRandomSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      randomSolution,
      problemInstance.cities -- randomSolution.path,
      LocalSearchWithNodesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsGreedyHeuristicStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val initialSolution =
      getGreedyAnyPositionSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path,
      LocalSearchWithNodesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsSteepestRandomStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val randomSolution = getRandomSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      randomSolution,
      problemInstance.cities -- randomSolution.path,
      LocalSearchWithNodesSwapsSteepest.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsSteepestHeuristicStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val initialSolution =
      getGreedyAnyPositionSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path,
      LocalSearchWithNodesSwapsSteepest.updateSolution
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
