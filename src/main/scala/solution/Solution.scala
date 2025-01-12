import scala.util.Random
import scala.util.control.TailCalls.TailRec
import scala.annotation.tailrec

case class AdditionalData(numOfIterations: Option[Int] = None)
case class Solution(
    path: Seq[Int],
    cost: Int,
    additionalData: Option[AdditionalData] = None
)

object SolutionGenerator extends CostManager {
  def generateRandomSolution(): Solution = RandomGenerator.generate()

  def generateSemiRandomSolution(numberOfCitiesToSwap: Int): Solution = {
    val initialSolution = generateInsertAnyPositionSolution()
    val shuffledPath = Random.shuffle(
      (ProblemInstanceHolder.problemInstance.cities -- initialSolution.path).toSeq
    )
    val randomizedPath = shuffledPath.take(numberOfCitiesToSwap) ++
      shuffledPath.drop(numberOfCitiesToSwap)
    Solution(
      randomizedPath,
      calculateSolutionCost(randomizedPath)
    )
  }

  def generateTailAppendSolution(): Solution = {
    val initialCity =
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)

    generateSolution(
      Solution(
        Seq(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      TailAppendGenerator.updateSolution
    )
  }

  def generateInsertAnyPositionSolution(): Solution = {
    val initialCity =
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)

    generateSolution(
      Solution(
        Seq(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      InsertAnyPositionGenerator.updateSolution
    )
  }

  def generateCycleSolution(): Solution = {
    val initialCity =
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)

    generateSolution(
      Solution(
        Seq(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      CycleGenerator.updateSolution
    )
  }

  def generateCycleRegretSolution(): Solution = {
    val initialCity =
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)

    generateSolution(
      Solution(
        Seq(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      CycleRegretGenerator.updateSolution
    )
  }

  def generateCycleWeightedRegretSolution(): Solution = {
    val initialCity =
      Random.nextInt(ProblemInstanceHolder.problemInstance.cities.size)

    generateSolution(
      Solution(
        Seq(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      CycleWeightedRegretGenerator.updateSolution _
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

object SolutionModifier extends MoveGenerator {
  def getLocalSearchGreedy(
      initialSolutionGenerator: => Solution
  ): Solution = {
    val initialSolution = initialSolutionGenerator
    val (modifiedSolution, modifiedAvailableCities) =
      LocalSearchGreedy.modifySolution(
        initialSolution,
        ProblemInstanceHolder.problemInstance.cities -- initialSolution.path
      )
    modifiedSolution
  }

  def getLocalSearchSteepest(
      initialSolutionGenerator: => Solution,
      neighbourhoodGenerator: (Solution, Set[Int]) => Seq[Move]
  ): Solution = {
    val initialSolution = initialSolutionGenerator
    val (modifiedSolution, modifiedAvailableCities) =
      LocalSearchSteepest.modifySolution(
        initialSolution,
        ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
        neighbourhoodGenerator
      )
    modifiedSolution
  }

  def getLocalSearchWithListOfImprovingMoves(
      initialSolutionGenerator: => Solution
  ): Solution = {
    val solution = initialSolutionGenerator
    val localSearchInstance = ListOfImprovingMovesSolution(
      solution,
      ProblemInstanceHolder.problemInstance.cities -- solution.path
    )
    modifySolution(
      solution,
      ProblemInstanceHolder.problemInstance.cities -- solution.path,
      localSearchInstance.updateSolution
    )
  }

  def getIteratedLocalSearch(
      initialSolutionGenerator: => Solution
  ): Solution = {
    val initialSolution = initialSolutionGenerator
    val updatedSolution = IteratedLSSolution.updateSolution(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path
    )
    updatedSolution
  }

  def getMSLS(): Solution = { MSLS.run() }

  def getLargeNeighborhoodSearchWithLocalSearch(): Solution = {
    val initialSolution = SolutionGenerator.generateRandomSolution()
    val startingTime = System.currentTimeMillis()
    val updatedSolution =
      LargeNeighborhoodSearch.performWithLocalSearch(
        initialSolution,
        ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
        System.currentTimeMillis() - startingTime > 22215
      )
    updatedSolution
  }

  def getLargeNeighborhoodSearchWithoutLocalSearch(): Solution = {
    val initialSolution = SolutionGenerator.generateRandomSolution()
    val startingTime = System.currentTimeMillis()
    val updatedSolution =
      LargeNeighborhoodSearch.performWithoutLocalSearch(
        initialSolution,
        ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
        System.currentTimeMillis() - startingTime > 22215
      )
    updatedSolution
  }

  def getHybridEvolutionaryRandom(): Solution = {
    HybridEvolutionary.searchRandom()
  }

  def getHybridEvolutionaryHeuristic(): Solution = {
    HybridEvolutionary.searchHeuristic()
  }

  @tailrec
  def modifySolutionNeighbourhood(
      currentSolution: Solution,
      remainingCities: Set[Int],
      solutionUpdater: (
          Solution,
          Set[Int],
          (Solution, Set[Int]) => Seq[Move]
      ) => (Solution, Set[Int]),
      neighbourhoodGenerator: (Solution, Set[Int]) => Seq[Move]
  ): Solution = {
    val (updatedSolution, updatedRemainingCities) =
      solutionUpdater(currentSolution, remainingCities, neighbourhoodGenerator)

    if (updatedSolution.cost == currentSolution.cost) {
      currentSolution
    } else {
      modifySolutionNeighbourhood(
        updatedSolution,
        updatedRemainingCities,
        solutionUpdater,
        neighbourhoodGenerator
      )
    }
  }

  @tailrec
  def modifySolution(
      currentSolution: Solution,
      remainingCities: Set[Int],
      solutionUpdater: (Solution, Set[Int]) => (Solution, Set[Int])
  ): Solution = {
    val (updatedSolution, updatedRemainingCities) =
      solutionUpdater(currentSolution, remainingCities)

    if (updatedSolution.cost == currentSolution.cost) {
      currentSolution
    } else {
      modifySolution(updatedSolution, updatedRemainingCities, solutionUpdater)
    }
  }
}
