import scala.collection.mutable
import scala.util.Random

object IteratedLSSolution
    extends MoveGenerator
    with MoveOperations
    with CostManager {

  def updateSolution(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Solution = {
    var bestSolution = currentSolution
    var bestAvailableCities = availableCities
    var numOfIterations = 0
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < 22150) {
      val (perturbedSolution, newAvailableCities) =
        perturbSolution(bestSolution, bestAvailableCities)
      val updatedSolution =
        SolutionModifier.getLocalSearchWithListOfImprovingMoves(
          perturbedSolution
        )
      if (updatedSolution.cost < bestSolution.cost) {
        bestSolution = updatedSolution
        bestAvailableCities =
          ProblemInstanceHolder.problemInstance.cities -- updatedSolution.path
      }
      numOfIterations += 1
    }
    bestSolution.copy(
      additionalData =
        Some(AdditionalData(numOfIterations = Some(numOfIterations)))
    )
  }

  def perturbSolution(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    // assert(availableCities.filter(currentSolution.path.contains).isEmpty)
    var n = 2
    var perturbedSolution: Solution = currentSolution
    var perturbedAvailableCities: Set[Int] = availableCities
    for (_ <- 1 to n) {
      val triplet =
        Random
          .shuffle(getCycleConsecutiveTriplets(perturbedSolution.path))
          .head
      val pair =
        Random.shuffle(getCycleConsecutivePairs(perturbedSolution.path)).head
      val city = Random.shuffle(perturbedAvailableCities.toSeq).head
      if (
        Set(
          triplet.city1,
          triplet.city2,
          triplet.city3,
          pair.city1,
          pair.city2,
          city
        ).size != 6
      ) {
        n += 1
      } else {
        val move = TwoNodeExchange(triplet, pair, city)
        val (newPerturbedSolution, newAvailableCities) =
          performMove(perturbedSolution, move, perturbedAvailableCities)
        perturbedSolution = newPerturbedSolution.copy(
          cost = perturbedSolution.cost + getDeltaCost(move)
        )
        perturbedAvailableCities = newAvailableCities
      }
    }
    (perturbedSolution, perturbedAvailableCities)
  }
}
