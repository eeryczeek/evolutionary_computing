import scala.util.Random

object LargeNeighborhoodSearch
    extends MoveGenerator
    with MoveOperations
    with CostManager {

  def performWithLocalSearch(
      initialSolution: Solution,
      availableCities: Set[Int],
      stoppingCondition: => Boolean
  ): Solution = {
    var currentSolution = initialSolution
    var noOfIterations = 0
    while (!stoppingCondition) {
      val (destroyedSolution, destroyedAvailableCities) =
        destroy(currentSolution)
      val (repairedSolution, repairedAvailableCities) =
        repair(destroyedSolution, destroyedAvailableCities)
      val localSearchSolution =
        SolutionModifier.getLocalSearchWithListOfImprovingMoves(
          repairedSolution
        )
      if (localSearchSolution.cost < currentSolution.cost) {
        currentSolution = localSearchSolution
      }
      noOfIterations += 1
    }

    currentSolution.copy(additionalData =
      Some(AdditionalData(numOfIterations = Some(noOfIterations)))
    )
  }

  def performWithoutLocalSearch(
      initialSolution: Solution,
      availableCities: Set[Int],
      stoppingCondition: => Boolean
  ): Solution = {
    var currentSolution = initialSolution
    var noOfIterations = 0
    currentSolution =
      SolutionModifier.getLocalSearchWithListOfImprovingMoves(currentSolution)
    while (!stoppingCondition) {
      val (destroyedSolution, destroyedAvailableCities) =
        destroy(currentSolution)
      val (repairedSolution, repairedAvailableCities) =
        repair(destroyedSolution, destroyedAvailableCities)
      if (repairedSolution.cost < currentSolution.cost) {
        currentSolution = repairedSolution
      }
      noOfIterations += 1
    }

    currentSolution.copy(additionalData =
      Some(AdditionalData(numOfIterations = Some(noOfIterations)))
    )
  }

  def destroy(
      currentSolution: Solution
  ): (Solution, Set[Int]) = {
    val citiesToRemove = getCitiesToRemove(currentSolution)
    val newSolution = Solution(
      currentSolution.path.filterNot(citiesToRemove.contains),
      calculateSolutionCost(
        currentSolution.path.filterNot(citiesToRemove.contains)
      )
    )
    (
      newSolution,
      ProblemInstanceHolder.problemInstance.cities -- newSolution.path
    )
  }

  def getCitiesToRemove(
      currentSolution: Solution
  ): Set[Int] = {
    // val citiesToRemove = collection.mutable.Set[Int]()
    // val distributionBasedOnCityCosts = currentSolution.path.map { city =>
    //   ProblemInstanceHolder.problemInstance.cityCosts(city)
    // }
    // val totalCost = distributionBasedOnCityCosts.sum
    // val distribution = distributionBasedOnCityCosts.map { cost =>
    //   cost / totalCost
    // }
    // val cumulativeDistribution = distribution.scanLeft(0.0)(_ + _).tail
    // var tries = 0
    // while (citiesToRemove.size < 30 && tries < 40) {
    //   val randomValue = Random.nextDouble()
    //   val randomCityIdx = cumulativeDistribution.indexWhere { value =>
    //     value > randomValue
    //   }
    //   val randomCity =
    //     currentSolution.path(if (randomCityIdx == -1) 99 else randomCityIdx)
    //   citiesToRemove.add(randomCity)
    //   tries += 1
    // }
    val citiesToRemove = Random.shuffle(currentSolution.path).take(30).toSet
    citiesToRemove
  }

  def repair(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val repairedSolution = SolutionGenerator.generateSolution(
      currentSolution,
      availableCities,
      InsertAnyPositionGenerator.updateSolution
    )

    (
      repairedSolution,
      ProblemInstanceHolder.problemInstance.cities -- repairedSolution.path
    )
  }
}
