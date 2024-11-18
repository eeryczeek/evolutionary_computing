import scala.collection.mutable
import scala.util.Random

class ListOfImprovingMovesSolution(problemInstance: ProblemInstance)
    extends LocalSearch
    with MoveOperations
    with CostManager {

  private var improvingMoves = mutable.PriorityQueue[(Move, Int)]()(
    Ordering.by((_: (Move, Int))._2).reverse
  )

  private var lastDiff = 0

  def init(initialSolution: Solution, availableCities: Set[Int]): Unit = {
    val edgeSwaps =
      getAllEdgeSwaps(problemInstance, initialSolution, availableCities)
    val nodeSwapsOut =
      getAllNodeSwapsOut(problemInstance, initialSolution, availableCities)
    val possibleMoves = edgeSwaps ++ nodeSwapsOut

    val improvingMovesWithCosts = possibleMoves
      .map(move => (move, getDeltaCost(problemInstance, move)))
      .filter(_._2 < 0)

    improvingMoves.enqueue(improvingMovesWithCosts: _*)
  }

  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val edges = getConsecutivePairs(currentSolution).toSet
    val triplets = getConsecutiveTriplets(currentSolution).toSet
    improvingMoves.find { case (move, _) =>
      isMoveApplicable(move, currentSolution, edges, triplets)
    } match {
      case Some((move, deltaCost)) =>
        val trueMove = move match {
          case EdgeSwap(edge1, edge2) =>
            if (edges.contains(edge1)) move
            else
              EdgeSwap(
                Pair(edge1.city2, edge1.city1),
                Pair(edge2.city2, edge2.city1)
              )
          case NodeSwapOut(triplet, city) =>
            if (triplets.contains(triplet)) move
            else
              NodeSwapOut(
                Triplet(triplet.city3, triplet.city2, triplet.city1),
                city
              )
        }
        val (newSolution, newAvailableCities) = {
          performMove(currentSolution, trueMove, availableCities)
        }

        // {
        //   val newCost = Cost.calculateSolutionCost(problemInstance, newSolution)
        //   val oldCost = currentSolution.cost
        //   val diffBetweenCosts = newCost - (oldCost + deltaCost)
        //   if (diffBetweenCosts != 0) {
        //     throw new Exception(
        //       s"diff between costs is not 0: $diffBetweenCosts"
        //     )
        //   }
        //   val fullNeighbourhood = getAllEdgeSwaps(
        //     problemInstance,
        //     newSolution,
        //     newAvailableCities
        //   ) ++ getAllNodeSwapsOut(
        //     problemInstance,
        //     newSolution,
        //     newAvailableCities
        //   )
        //   val numberOfImprovingMoves = fullNeighbourhood
        //     .map(move => (move, getDeltaCost(problemInstance, move)))
        //     .count(_._2 < 0)
        //
        //   val numberOfMovesWithWrongCost = fullNeighbourhood
        //     .map(move => (move, getDeltaCost(problemInstance, move)))
        //     .filter { case (move, cost) =>
        //       cost < 0 && improvingMoves
        //         .find(_._1 == move)
        //         .map(_._2 != cost)
        //         .getOrElse(true)
        //     }
        //     .count(_ => true)
        //
        //   val freshDeltaCost = getDeltaCost(problemInstance, trueMove)
        //   println(
        //     s"Move: $move, delta cost $deltaCost, cost old: $oldCost, real new cost: $newCost, diff: $diffBetweenCosts"
        //   )
        //   println(
        //     s"number of improving moves: ${improvingMoves.size}, expected: $numberOfImprovingMoves"
        //   )
        //
        //   if (
        //     currentSolution.path.distinct.size != newSolution.path.distinct.size
        //   ) {
        //     println(s"faulty move introduced duplicates: $move")
        //     println(
        //       s"current Solution:\n ${currentSolution.path.mkString(", ")}"
        //     )
        //     println(s"new Solution:\n ${newSolution.path.mkString(", ")}")
        //   }
        //   if (diffBetweenCosts != lastDiff) {
        //     println(
        //       s"current Solution:\n ${currentSolution.path.mkString(", ")}"
        //     )
        //     println(s"new Solution:\n ${newSolution.path.mkString(", ")}")
        //     lastDiff = diffBetweenCosts
        //   }
        //   println
        // }

        improvingMoves = improvingMoves.filter { case (m, _) =>
          m != move
        }

        updateImprovingMoves(
          problemInstance,
          newSolution,
          newAvailableCities,
          trueMove
        )

        (
          newSolution.copy(cost = currentSolution.cost + deltaCost),
          newAvailableCities
        )
      case None =>
        (currentSolution, availableCities)
    }
  }

  def isMoveApplicable(
      move: Move,
      currentSolution: Solution,
      allEdges: Set[Pair],
      allTriplets: Set[Triplet]
  ): Boolean = {
    move match {
      case EdgeSwap(edge1, edge2) => {
        val reversedEdge1 = Pair(edge1.city2, edge1.city1)
        val reversedEdge2 = Pair(edge2.city2, edge2.city1)
        (allEdges.contains(edge1) && allEdges.contains(edge2)) ||
        (allEdges.contains(reversedEdge1) && allEdges.contains(reversedEdge2))
      }
      case NodeSwapOut(triplet, city) => {
        val reversedTriplet =
          Triplet(triplet.city3, triplet.city2, triplet.city1)
        allTriplets.contains(triplet) || allTriplets.contains(reversedTriplet)
      }
      case _ => false
    }
  }

  def updateImprovingMoves(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int],
      move: Move
  ): Unit = {
    move match {
      case EdgeSwap(edge1, edge2) =>
        val reversedEdge1 = Pair(edge1.city2, edge1.city1)
        val reversedEdge2 = Pair(edge2.city2, edge2.city1)
        val edgeMappings = Map(
          edge1 -> Pair(edge1.city1, edge2.city1),
          edge2 -> Pair(edge2.city2, edge1.city2),
          reversedEdge1 -> Pair(edge2.city1, edge1.city1),
          reversedEdge2 -> Pair(edge1.city2, edge2.city2)
        )
        val mapCities = Map(
          edge1.city1 -> edge2.city1,
          edge1.city2 -> edge2.city2,
          edge2.city1 -> edge1.city1,
          edge2.city2 -> edge1.city2
        )
        val citiesInRemovedEdges =
          Set(edge1.city1, edge1.city2, edge2.city1, edge2.city2)

        improvingMoves = improvingMoves
          // .mapInPlace {
          //   case (EdgeSwap(e1, e2), _)
          //       if (e1 == edge1 || e1 == edge2 || e1 == reversedEdge1 || e1 == reversedEdge2) || (e2 == edge2 || e2 == edge1 || e2 == reversedEdge1 || e2 == reversedEdge2) => {
          //     val newMove = EdgeSwap(
          //       edgeMappings
          //         .getOrElse(e1, e1),
          //       edgeMappings
          //         .getOrElse(e2, e2)
          //     )
          //     (newMove, getDeltaCost(problemInstance, newMove))
          //   }
          //   case (NodeSwapOut(triplet, city), _)
          //       if tripletContainsEdge(triplet, edge1) ||
          //         tripletContainsEdge(triplet, edge2) => {
          //     val newMove = NodeSwapOut(
          //       Triplet(
          //         mapCities.getOrElse(triplet.city1, triplet.city1),
          //         mapCities.getOrElse(triplet.city2, triplet.city2),
          //         mapCities.getOrElse(triplet.city3, triplet.city3)
          //       ),
          //       city
          //     )
          //     (newMove, getDeltaCost(problemInstance, newMove))
          //   }
          //
          //   case move => move
          // }
          // .filter(_._2 < 0)
          .filter {
            case (EdgeSwap(e1, e2), _) =>
              !(e1 == edge1 || e1 == edge2 || e2 == edge1 || e2 == edge2)
            case (NodeSwapOut(triplet, city), _) =>
              !tripletContainsAnyCity(triplet, citiesInRemovedEdges)
            case _ => true
          }

        val newEdgeSwaps = getAllEdgeSwapsForEdge(
          currentSolution,
          Pair(edge1.city1, edge2.city1)
        ) ++ getAllEdgeSwapsForEdge(
          currentSolution,
          Pair(edge1.city2, edge2.city2)
        )

        val newEdgeSwapsWithCosts = newEdgeSwaps
          .map(move => (move, getDeltaCost(problemInstance, move)))
          .filter(_._2 < 0)

        val newNodeSwapsOut = getAllNodeSwapsOutForRemovedCities(
          currentSolution,
          citiesInRemovedEdges,
          availableCities
        ).map(move => (move, getDeltaCost(problemInstance, move)))
          .filter(_._2 < 0)

        val newMovesCombined = newEdgeSwapsWithCosts ++ newNodeSwapsOut
        improvingMoves.addAll(newMovesCombined)

      case NodeSwapOut(Triplet(_, removedCity, _), addedCity) =>
        improvingMoves = improvingMoves
          .mapInPlace {
            case (move @ NodeSwapOut(_, city), _) if city == addedCity => {
              (move, 1)
            }

            case (NodeSwapOut(triplet, city), _)
                if tripletContainsCity(triplet, removedCity) => {
              val newMove = NodeSwapOut(
                triplet match {
                  case Triplet(a, b, c) if a == removedCity =>
                    Triplet(addedCity, b, c)
                  case Triplet(a, b, c) if b == removedCity =>
                    Triplet(a, addedCity, c)
                  case Triplet(a, b, c) if c == removedCity =>
                    Triplet(a, b, addedCity)
                },
                city
              )
              (newMove, getDeltaCost(problemInstance, newMove))
            }

            case (EdgeSwap(edge1, edge2), _)
                if edge1.city1 == removedCity || edge1.city2 == removedCity || edge2.city1 == removedCity || edge2.city2 == removedCity => {
              val newMove = EdgeSwap(
                Pair(
                  if (edge1.city1 == removedCity) addedCity else edge1.city1,
                  if (edge1.city2 == removedCity) addedCity else edge1.city2
                ),
                Pair(
                  if (edge2.city1 == removedCity) addedCity else edge2.city1,
                  if (edge2.city2 == removedCity) addedCity else edge2.city2
                )
              )
              (newMove, getDeltaCost(problemInstance, newMove))
            }

            case move => move
          }
          .filter(_._2 < 0)
    }
  }

  private def tripletContainsEdge(triplet: Triplet, edge: Pair): Boolean = {
    triplet.city1 == edge.city1 || triplet.city1 == edge.city2 ||
    triplet.city2 == edge.city1 || triplet.city2 == edge.city2 ||
    triplet.city3 == edge.city1 || triplet.city3 == edge.city2
  }

  private def tripletContainsCity(triplet: Triplet, city: Int): Boolean = {
    triplet.city1 == city || triplet.city2 == city || triplet.city3 == city
  }

  private def tripletContainsAnyCity(
      triplet: Triplet,
      cities: Set[Int]
  ): Boolean = {
    cities.contains(triplet.city1) ||
    cities.contains(triplet.city2) ||
    cities.contains(triplet.city3)
  }

  private def getAllEdgeSwapsForEdge(
      solution: Solution,
      edge: Pair
  ): List[EdgeSwap] = {
    val edges = getConsecutivePairs(solution).toSet
    edges
      .filter(e =>
        e != edge && List(
          e.city1,
          e.city2,
          edge.city1,
          edge.city2
        ).distinct.size == 4
      )
      .map(e => EdgeSwap(edge, e))
      .toList
  }

  private def getAllNodeSwapsOutForRemovedCities(
      solution: Solution,
      citiesFromRemovedEdges: Set[Int],
      availableCities: Set[Int]
  ): Seq[NodeSwapOut] = {
    val triplets = getConsecutiveTriplets(solution).toSet
    triplets
      .filter(t =>
        citiesFromRemovedEdges.contains(t.city1) ||
          citiesFromRemovedEdges.contains(t.city2) ||
          citiesFromRemovedEdges.contains(t.city3)
      )
      .flatMap { triplet =>
        availableCities.map(city => NodeSwapOut(triplet, city))
      }
      .toSeq
  }
}

object ListOfImprovingMovesSolution {
  def apply(
      problemInstance: ProblemInstance,
      initialSolution: Solution,
      availableCities: Set[Int]
  ): ListOfImprovingMovesSolution = {
    val instance = new ListOfImprovingMovesSolution(problemInstance)
    instance.init(initialSolution, availableCities)
    instance
  }

}
