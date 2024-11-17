import scala.util.Random
import java.nio.file.AtomicMoveNotSupportedException

trait LocalSearch extends MoveOperations with CostManager {

  def generateCandidateMoves(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    for (triplet <- getConsecutiveTriplets(currentSolution)) {
      for (city <- problemInstance.candidateEdges(triplet.city2)) {
        if (currentSolution.path.contains(city2)) {
          getCandidateEdgeSwap(currentSolution, city1, city2)
        } else {
          getCandidateNodeSwap(currentSolution, city1, city2)
        }
      }
    }
  }

  def getCandidateEdgeSwap(
      currentSolution: Solution,
      city1: Int,
      city2: Int
  ): {
    city1Index = currentSolution.path.indexOf(city1)
    city2Index = currentSolution.path.indexOf(city2)
    
  }

  def getAllNodeSwapsOut(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    for {
      triplet <- getConsecutiveTriplets(currentSolution)
      city <- availableCities
    } yield NodeSwapOut(triplet, city)
  }

  def getAllEdgeSwaps(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    for {
      pair1 <- getConsecutivePairs(currentSolution)
      pair2 <- getConsecutivePairs(currentSolution)
      if Set(pair1.city1, pair1.city2, pair2.city1, pair2.city2).size == 4
    } yield EdgeSwap(pair1, pair2)
  }

  def getAllNodeSwapsIn(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    for {
      pair1 <- getConsecutivePairs(currentSolution)
      pair2 <- getConsecutivePairs(currentSolution)
      if Set(pair1.city1, pair1.city2, pair2.city1, pair2.city2).size == 4
    } yield EdgeSwap(pair1, pair2)
  }

  def getCandidateMoves(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    val possibleMoves = getNeighbourhoodWithEdgesSwapsIn(
      problemInstance,
      currentSolution,
      availableCities
    )
    val candidateEdges = problemInstance.candidateEdges
    possibleMoves.filter(move => anyEdgeInCandidateEdges(candidateEdges, move))
  }

  def anyEdgeInCandidateEdges(
      candidateEdges: Array[Set[Int]],
      move: Move
  ): Boolean = {
    move match {
      case EdgeSwap(pair1, pair2) =>
        candidateEdges(pair1.city1).contains(pair2.city1) ||
        candidateEdges(pair2.city1).contains(pair1.city1) ||
        candidateEdges(pair1.city2).contains(pair2.city2) ||
        candidateEdges(pair2.city2).contains(pair1.city2)

      case NodeSwapOut(triplet, city) =>
        candidateEdges(triplet.city1).contains(city) ||
        candidateEdges(triplet.city3).contains(city)

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
