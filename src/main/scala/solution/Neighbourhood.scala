import scala.util.Random

trait LocalSearch extends MoveOperations with CostManager {
  def getNeighbourhoodWithEdgesSwapsIn(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    val path = currentSolution.path.toArray
    val pairs = getConsecutivePairs(currentSolution)
    val triplets = getConsecutiveTriplets(currentSolution)

    val edgeSwapsIn = pairs
      .combinations(2)
      .collect {
        case Array(pair1, pair2)
            if Set(
              pair1.city1,
              pair1.city2,
              pair2.city1,
              pair2.city2
            ).size == 4 =>
          EdgeSwap(pair1, pair2)
      }
      .toSeq

    val nodeSwapsOut = availableCities
      .flatMap(city => triplets.map(triplet => NodeSwapOut(triplet, city)))
      .toSeq

    edgeSwapsIn ++ nodeSwapsOut
  }

  def getNeighbourhoodWithNodesSwapsIn(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    val path = currentSolution.path.toArray
    val triplets = getConsecutiveTriplets(currentSolution)

    val nodeSwapsIn = triplets
      .combinations(2)
      .collect {
        case Array(triplet1, triplet2) if !triplet1.equals(triplet2) =>
          NodeSwapIn(triplet1, triplet2)
      }
      .toSeq

    val nodeSwapsOut = availableCities
      .flatMap(city => triplets.map(triplet => NodeSwapOut(triplet, city)))
      .toSeq

    nodeSwapsIn ++ nodeSwapsOut
  }
}
