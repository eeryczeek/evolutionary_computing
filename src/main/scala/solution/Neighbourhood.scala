import scala.util.Random

sealed trait Move
case class EdgeSwap(edge1: (Int, Int), edge2: (Int, Int)) extends Move
case class NodeSwapIn(triplet1: (Int, Int, Int), triplet2: (Int, Int, Int))
    extends Move
case class NodeSwapOut(triplet: (Int, Int, Int), city: Int) extends Move

trait LocalSearch {
  def getNeighbourhood(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): Seq[Move] = {
    val pairs = (currentSolution.path :+ currentSolution.path.head)
      .sliding(2)
      .map { case List(a, b) => (a, b) }
      .toList

    val triplets = (currentSolution.path ++ currentSolution.path.take(2))
      .sliding(3)
      .map { case List(a, b, c) => (a, b, c) }
      .toList

    val edgeSwapsIn = pairs
      .combinations(2)
      .collect {
        case Seq(pair1, pair2)
            if List(pair1._1, pair1._2, pair2._1, pair2._2).toSet.size == 4 =>
          EdgeSwap(pair1, pair2)
      }
      .toSeq

    val nodeSwapsIn = triplets
      .combinations(2)
      .collect {
        case Seq(triplet1, triplet2) if triplet1 != triplet2 =>
          NodeSwapIn(triplet1, triplet2)
      }
      .toSeq

    val nodeSwapsOut = availableCities
      .flatMap(city => triplets.map(triplet => NodeSwapOut(triplet, city)))
      .toSeq

    edgeSwapsIn ++ nodeSwapsIn ++ nodeSwapsOut
  }

  def getDeltaCost(
      problemInstance: ProblemInstance,
      move: Move
  ): Int = {
    val distances = problemInstance.distances
    move match {
      case EdgeSwap((city1, city2), (city3, city4)) =>
        distances(city1)(city3) +
          distances(city2)(city4) -
          distances(city1)(city2) -
          distances(city3)(city4)

      case NodeSwapIn((city1, city2, city3), (city4, city5, city6)) =>
        distances(city1)(city5) +
          distances(city5)(city3) +
          distances(city4)(city2) +
          distances(city2)(city6) -
          distances(city1)(city2) -
          distances(city2)(city3) -
          distances(city4)(city5) -
          distances(city5)(city6)

      case NodeSwapOut((city1, city2, city3), city4) =>
        distances(city1)(city4) +
          distances(city4)(city3) +
          problemInstance.cityCosts(city4) -
          distances(city1)(city2) -
          distances(city2)(city3) -
          problemInstance.cityCosts(city2)
    }
  }

  def updateSolutionWithMove(
      currentSolution: Solution,
      move: Move,
      deltaCost: Int,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val newPath = move match {
      case EdgeSwap((city1, city2), (city3, city4)) =>
        val city1Position = currentSolution.path.indexOf(city1)
        val city2Position = currentSolution.path.indexOf(city3)

        val (start, end) = if (city1Position < city2Position) {
          (city1Position + 1, city2Position + 1)
        } else {
          (city2Position + 1, city1Position + 1)
        }

        val sublistToReverse = currentSolution.path.slice(start, end)
        currentSolution.path.patch(
          start,
          sublistToReverse.reverse,
          sublistToReverse.length
        )

      case NodeSwapIn((city1, city2, city3), (city4, city5, city6)) =>
        val city2index = currentSolution.path.indexOf(city2)
        val city5index = currentSolution.path.indexOf(city5)
        currentSolution.path
          .updated(city2index, city5)
          .updated(city5index, city2)

      case NodeSwapOut((city1, city2, city3), city4) =>
        currentSolution.path
          .updated(currentSolution.path.indexOf(city2), city4)
    }

    val newSolution = Solution(newPath, currentSolution.cost + deltaCost)
    val newAvailableCities = move match {
      case NodeSwapOut((_, city2, _), city4) => availableCities + city2 - city4
      case _                                 => availableCities
    }

    (newSolution, newAvailableCities)
  }
}
