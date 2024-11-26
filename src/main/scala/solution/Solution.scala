import scala.util.Random
import scala.util.control.TailCalls.TailRec
import scala.annotation.tailrec

case class Solution(path: Seq[Int], cost: Int)

object SolutionFactory {
  def getRandomSolution(): Solution =
    SolutionGenerator.generateRandomSolution()

  def getGreedyTailSolution(): Solution =
    SolutionGenerator.generateGreedyTailSolution()

  def getGreedyAnyPositionSolution(): Solution =
    SolutionGenerator.generateGreedyAnyPositionSolution()

  def getGreedyCycleSolution(): Solution =
    SolutionGenerator.getGreedyCycleSolution()

  def getGreedyCycleRegretSolution(): Solution =
    SolutionGenerator.getGreedyCycleRegretSolution()

  def getGreedyCycleWeightedRegretSolution(): Solution =
    SolutionGenerator.getGreedyCycleWeightedRegretSolution()

  def getLocalSearchWithEdgesSwapsGreedy(
      initialSolutionGenerator: () => Solution
  ): Solution =
    SolutionUpdater.getLocalSearchWithEdgesSwapsGreedy(
      initialSolutionGenerator = initialSolutionGenerator
    )

  def getLocalSearchWithEdgesSwapsSteepest(
      initialSolutionGenerator: () => Solution
  ): Solution =
    SolutionUpdater.getLocalSearchWithEdgesSwapsSteepest(
      initialSolutionGenerator = initialSolutionGenerator
    )

  def getLocalSearchWithNodesSwapsGreedy(
      initialSolutionGenerator: () => Solution
  ): Solution =
    SolutionUpdater.getLocalSearchWithNodesSwapsGreedy(
      initialSolutionGenerator = initialSolutionGenerator
    )

  def getLocalSearchWithNodesSwapsSteepest(
      initialSolutionGenerator: () => Solution
  ): Solution =
    SolutionUpdater.getLocalSearchWithNodesSwapsSteepest(
      initialSolutionGenerator = initialSolutionGenerator
    )

  def getLocalsearchWithCandidateMovesGreedy(
      initialSolutionGenerator: () => Solution
  ): Solution =
    SolutionUpdater.getLocalsearchWithCandidateMovesGreedy(
      initialSolutionGenerator = initialSolutionGenerator
    )

  def getLocalsearchWithCandidateMovesSteepest(
      initialSolutionGenerator: () => Solution
  ): Solution =
    SolutionUpdater.getLocalsearchWithCandidateMovesSteepest(
      initialSolutionGenerator = initialSolutionGenerator
    )

  def getLocalSearchWithListOfImprovingMoves(
      initialSolutionGenerator: () => Solution
  ): Solution =
    SolutionUpdater.getLocalSearchWithListOfImprovingMoves(
      initialSolutionGenerator = initialSolutionGenerator
    )

  def getIteratedLocalSearch(): Solution =
    SolutionUpdater.getIteratedLocalSearch()

  def getMSLS(): Solution = SolutionUpdater.getMSLS()
}

object SolutionGenerator extends CostManager {
  def generateRandomSolution(): Solution = RandomSolution.generate()

  def generateGreedyTailSolution(): Solution = {
    val initialCity =
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)

    generateSolution(
      Solution(
        Seq(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyTailSolution.updateSolution
    )
  }

  def generateGreedyAnyPositionSolution(): Solution = {
    val initialCity =
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)

    generateSolution(
      Solution(
        Seq(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyAtAnyPositionSolution.updateSolution
    )
  }

  def getGreedyCycleSolution(): Solution = {
    val initialCity =
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)

    generateSolution(
      Solution(
        Array(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyCycleSolution.updateSolution
    )
  }

  def getGreedyCycleRegretSolution(): Solution = {
    val initialCity =
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)

    generateSolution(
      Solution(
        Array(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyCycleRegretSolution.updateSolution
    )
  }

  def getGreedyCycleWeightedRegretSolution(): Solution = {
    val initialCity =
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)

    generateSolution(
      Solution(
        Array(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyCycleWeightedRegretSolution.updateSolution _
    )
  }

  @tailrec
  def generateSolution(
      currentSolution: Solution,
      remainingCities: Set[Int],
      updateSolution: (Solution, Set[Int]) => (Solution, Set[Int])
  ): Solution = {
    if (
      currentSolution.path.size == ProblemInstanceHolder.problemInstance.expectedSolutionLen
    ) {
      val currentSolutionCost = calculateSolutionCost(currentSolution.path)
      currentSolution.copy(cost = currentSolutionCost)
    } else {
      val (updatedSolution, updatedRemainingCities) =
        updateSolution(currentSolution, remainingCities)
      generateSolution(updatedSolution, updatedRemainingCities, updateSolution)
    }
  }
}

object SolutionUpdater {
  def getLocalSearchWithEdgesSwapsGreedy(
      initialSolutionGenerator: () => Solution
  ): Solution = {
    val initialSolution = initialSolutionGenerator()
    updateSolution(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithEdgesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithEdgesSwapsSteepest(
      initialSolutionGenerator: () => Solution
  ): Solution = {
    val initialSolution = initialSolutionGenerator()
    updateSolution(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithEdgesSwapsSteepest.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsGreedy(
      initialSolutionGenerator: () => Solution
  ): Solution = {
    val initialSolution = initialSolutionGenerator()
    updateSolution(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithNodesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsSteepest(
      initialSolutionGenerator: () => Solution
  ): Solution = {
    val initialSolution = initialSolutionGenerator()
    updateSolution(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithNodesSwapsSteepest.updateSolution
    )
  }

  def getLocalsearchWithCandidateMovesGreedy(
      initialSolutionGenerator: () => Solution
  ): Solution = {
    val initialSolution = initialSolutionGenerator()
    updateSolution(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithCandidateMovesGreedy.updateSolution
    )
  }

  def getLocalsearchWithCandidateMovesSteepest(
      initialSolutionGenerator: () => Solution
  ): Solution = {
    val initialSolution = initialSolutionGenerator()
    updateSolution(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithCandidateMovesSteepest.updateSolution
    )
  }

  def getLocalSearchWithListOfImprovingMoves(
      initialSolutionGenerator: () => Solution
  ): Solution = {
    val solution = initialSolutionGenerator()
    val localSearchInstance = ListOfImprovingMovesSolution(
      solution,
      ProblemInstanceHolder.problemInstance.cities -- solution.path
    )
    updateSolution(
      solution,
      ProblemInstanceHolder.problemInstance.cities -- solution.path,
      localSearchInstance.updateSolution
    )
  }

  def getIteratedLocalSearch(): Solution = {
    val initialSolution = SolutionGenerator.generateRandomSolution()
    val updatedSolution = IteratedLSSolution.updateSolution(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path
    )
    updatedSolution
  }

  def getMSLS(): Solution = { MSLS.run() }

  @tailrec
  def updateSolution(
      currentSolution: Solution,
      remainingCities: Set[Int],
      solutionUpdater: (Solution, Set[Int]) => (Solution, Set[Int])
  ): Solution = {
    val (updatedSolution, updatedRemainingCities) =
      solutionUpdater(currentSolution, remainingCities)

    if (updatedSolution == currentSolution) {
      currentSolution
    } else {
      updateSolution(updatedSolution, updatedRemainingCities, solutionUpdater)
    }
  }
}
