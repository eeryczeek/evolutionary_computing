import scala.util.Random

trait LocalSearch extends MoveOperations with CostManager {

  def getCandidateMoves(
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

    val nodesSwapsOut = availableCities
      .flatMap(city => triplets.map(triplet => NodeSwapOut(triplet, city)))
      .toSeq

    val possibleMoves = edgeSwapsIn ++ nodesSwapsOut
    possibleMoves.filter(move => anyEdgeInCandidateEdges(problemInstance, move))
  }

  def anyEdgeInCandidateEdges(
      problemInstance: ProblemInstance,
      move: Move
  ): Boolean = {
    move match {
      case EdgeSwap(pair1, pair2) =>
        problemInstance.candidateEdges(pair1.city1).contains(pair2.city1) ||
        problemInstance.candidateEdges(pair2.city1).contains(pair1.city1) ||
        problemInstance.candidateEdges(pair1.city2).contains(pair2.city2) ||
        problemInstance.candidateEdges(pair2.city2).contains(pair1.city2)

      case NodeSwapOut(triplet, city) =>
        problemInstance.candidateEdges(triplet.city1).contains(city) ||
        problemInstance.candidateEdges(triplet.city3).contains(city)

      case _ => false
    }
  }

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
