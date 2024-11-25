import scala.util.Random
import scala.util.control.TailCalls.TailRec
import scala.annotation.tailrec

case class Solution(path: Array[Int], cost: Int)

object SolutionFactory {
  def getRandomSolution(): Solution =
    SolutionGenerator.getRandomSolution()

  def getAlredyFoundSolution(solution: Solution): Solution =
    SolutionGenerator.getAlreadyFoundSolution(solution)

  def getGreedyTailSolution(optionalInitialCity: Option[Int]): Solution =
    SolutionGenerator.getGreedyTailSolution(optionalInitialCity)

  def getGreedyAnyPositionSolution(optionalInitialCity: Option[Int]): Solution =
    SolutionGenerator.getGreedyAnyPositionSolution(optionalInitialCity)

  def getGreedyCycleSolution(optionalInitialCity: Option[Int]): Solution =
    SolutionGenerator.getGreedyCycleSolution(optionalInitialCity)

  def getGreedyCycleRegretSolution(optionalInitialCity: Option[Int]): Solution =
    SolutionGenerator.getGreedyCycleRegretSolution(optionalInitialCity)

  def getGreedyCycleWeightedRegretSolution(
      optionalInitialCity: Option[Int]
  ): Solution =
    SolutionGenerator.getGreedyCycleWeightedRegretSolution(optionalInitialCity)

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

object SolutionGenerator {
  def getRandomSolution(): Solution = {
    generate(
      Solution(Array.empty, 0),
      ProblemInstanceHolder.problemInstance.cities,
      RandomSolution.updateSolution
    )
  }

  def getAlreadyFoundSolution(solution: Solution): Solution = solution

  def getGreedyTailSolution(optionalInitialCity: Option[Int]): Solution = {
    val initialCity = optionalInitialCity.getOrElse(
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)
    )
    generate(
      Solution(
        Array(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyTailSolution.updateSolution
    )
  }

  def getGreedyAnyPositionSolution(
      optionalInitialCity: Option[Int]
  ): Solution = {
    val initialCity = optionalInitialCity.getOrElse(
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)
    )
    generate(
      Solution(
        Array(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyAtAnyPositionSolution.updateSolution
    )
  }

  def getGreedyCycleSolution(optionalInitialCity: Option[Int]): Solution = {
    val initialCity = optionalInitialCity.getOrElse(
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)
    )
    generate(
      Solution(
        Array(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyCycleSolution.updateSolution
    )
  }

  def getGreedyCycleRegretSolution(
      optionalInitialCity: Option[Int]
  ): Solution = {
    val initialCity = optionalInitialCity.getOrElse(
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)
    )
    generate(
      Solution(
        Array(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyCycleRegretSolution.updateSolution
    )
  }

  def getGreedyCycleWeightedRegretSolution(
      optionalInitialCity: Option[Int]
  ): Solution = {
    val initialCity = optionalInitialCity.getOrElse(
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)
    )
    generate(
      Solution(
        Array(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyCycleWeightedRegretSolution.updateSolution _
    )
  }

  @tailrec
  def generate(
      currentSolution: Solution,
      remainingCities: Set[Int],
      updateSolution: (Solution, Set[Int]) => (Solution, Set[Int])
  ): Solution = {
    val (updatedSolution, updatedRemainingCities) =
      updateSolution(currentSolution, remainingCities)

    if (updatedSolution == currentSolution) {
      currentSolution
    } else {
      generate(updatedSolution, updatedRemainingCities, updateSolution)
    }
  }
}

object SolutionUpdater {
  def getLocalSearchWithEdgesSwapsGreedy(
      initialSolutionGenerator: () => Solution
  ): Solution = {
    val initialSolution = initialSolutionGenerator()
    SolutionGenerator.generate(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithEdgesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithEdgesSwapsSteepest(
      initialSolutionGenerator: () => Solution
  ): Solution = {
    val initialSolution = initialSolutionGenerator()
    SolutionGenerator.generate(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithEdgesSwapsSteepest.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsGreedy(
      initialSolutionGenerator: () => Solution
  ): Solution = {
    val initialSolution = initialSolutionGenerator()
    SolutionGenerator.generate(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithNodesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsSteepest(
      initialSolutionGenerator: () => Solution
  ): Solution = {
    val initialSolution = initialSolutionGenerator()
    SolutionGenerator.generate(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithNodesSwapsSteepest.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsSteepestHeuristicStart(
      optionalInitialCity: Option[Int] = None,
      optionalInitialSolution: Option[Solution] = None
  ): Solution = {
    val initialCity = optionalInitialCity.getOrElse(
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)
    )
    val initialSolution =
      SolutionGenerator.getGreedyAnyPositionSolution(Some(initialCity))
    SolutionGenerator.generate(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithNodesSwapsSteepest.updateSolution
    )
  }

  def getLocalsearchWithCandidateMovesGreedy(
      initialSolutionGenerator: () => Solution
  ): Solution = {
    val initialSolution = initialSolutionGenerator()
    SolutionGenerator.generate(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithCandidateMovesGreedy.updateSolution
    )
  }

  def getLocalsearchWithCandidateMovesSteepest(
      initialSolutionGenerator: () => Solution
  ): Solution = {
    val initialSolution = initialSolutionGenerator()
    SolutionGenerator.generate(
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
    SolutionGenerator.generate(
      solution,
      ProblemInstanceHolder.problemInstance.cities -- solution.path,
      localSearchInstance.updateSolution
    )
  }

  def getIteratedLocalSearch(): Solution = {
    val initialSolution = SolutionGenerator.getRandomSolution()
    val updatedSolution = IteratedLSSolution.updateSolution(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path
    )
    updatedSolution
  }

  def getMSLS(): Solution = { MSLS.run() }
}
