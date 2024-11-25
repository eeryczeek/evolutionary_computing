import scala.util.Random
import java.nio.file.AtomicMoveNotSupportedException

trait LocalSearch extends MoveOperations with CostManager {
  def getCandidateEdgeSwap(
      currentSolution: Solution,
      city1: Int,
      city2: Int
  ): EdgeSwap = ???

  def getAllNodeSwapsOut(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    for {
      triplet <- getConsecutiveTriplets(currentSolution.path)
      city <- availableCities
    } yield NodeSwapOut(triplet, city)
  }

  def getAllEdgeSwaps(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    (for {
      pair1 <- getConsecutivePairs(currentSolution.path)
      pair2 <- getConsecutivePairs(currentSolution.path)
      if Set(pair1.city1, pair1.city2, pair2.city1, pair2.city2).size == 4
    } yield EdgeSwap(pair1, pair2)).toSeq
  }

  def getAllNodeSwapsIn(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    (for {
      pair1 <- getConsecutivePairs(currentSolution.path)
      pair2 <- getConsecutivePairs(currentSolution.path)
      if Set(pair1.city1, pair1.city2, pair2.city1, pair2.city2).size == 4
    } yield EdgeSwap(pair1, pair2)).toSeq
  }

  def getAllTwoNodeExchange(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    (for {
      triplet <- getConsecutiveTriplets(currentSolution.path)
      pair <- getConsecutivePairs(currentSolution.path)
      city <- availableCities
      if Set(
        triplet.city1,
        triplet.city2,
        triplet.city3,
        pair.city1,
        pair.city2,
        city
      ).size == 6
    } yield TwoNodeExchange(triplet, pair, city)).toSeq
  }

  def getCandidateMoves(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    val possibleMoves = getNeighbourhoodWithEdgesSwapsIn(
      currentSolution,
      availableCities
    )
    val candidateEdges = ProblemInstanceHolder.problemInstance.candidateEdges
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
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    val path = currentSolution.path.toArray
    val pairs = getConsecutivePairs(currentSolution.path)
    val triplets = getConsecutiveTriplets(currentSolution.path)

    val edgeSwapsIn = pairs
      .combinations(2)
      .collect {
        case Seq(pair1, pair2)
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
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    val path = currentSolution.path.toArray
    val triplets = getConsecutiveTriplets(currentSolution.path)

    val nodeSwapsIn = triplets
      .combinations(2)
      .collect {
        case Seq(triplet1, triplet2) if !triplet1.equals(triplet2) =>
          NodeSwapIn(triplet1, triplet2)
      }
      .toSeq

    val nodeSwapsOut = availableCities
      .flatMap(city => triplets.map(triplet => NodeSwapOut(triplet, city)))
      .toSeq

    nodeSwapsIn ++ nodeSwapsOut
  }
}
