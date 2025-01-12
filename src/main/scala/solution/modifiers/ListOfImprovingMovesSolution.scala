import scala.collection.mutable
import scala.util.Random

class ListOfImprovingMovesSolution()
    extends MoveGenerator
    with MoveOperations
    with CostManager {

  private implicit val ordering: Ordering[(Move, Int)] =
    Ordering.by((_: (Move, Int))._2).reverse
  private var improvingMoves = collection.mutable.PriorityQueue[(Move, Int)]()

  def init(initialSolution: Solution, availableCities: Set[Int]): Unit = {
    val edgeSwaps = getAllEdgeSwaps(initialSolution, availableCities)
    val invertedEdgeSwaps = edgeSwaps.map {
      case EdgeSwap(edge1, edge2) =>
        EdgeSwap(edge1, Pair(edge2.city2, edge2.city1))
      case _ => throw new Exception("Invalid move")
    }

    val nodeSwapsOut = getAllNodeSwapsOut(initialSolution, availableCities)
    val possibleMoves = edgeSwaps ++ invertedEdgeSwaps ++ nodeSwapsOut
    val improvingMovesWithCosts = possibleMoves
      .map(move => (move, getDeltaCost(move)))
      .filter(_._2 < 0)

    improvingMoves ++= improvingMovesWithCosts
  }

  def updateSolution(
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val edges = getCycleConsecutivePairs(currentSolution.path).toSet
    val triplets = getCycleConsecutiveTriplets(currentSolution.path).toSet
    findFirstApplicableMove(isMoveApplicable(_, edges, triplets)) match {
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
          case _ => move
        }
        val (newSolution, newAvailableCities) = {
          performMove(currentSolution, trueMove, availableCities)
        }

        improvingMoves = improvingMoves.filter { case (m, _) => m != move }
        updateImprovingMoves(newSolution, newAvailableCities, trueMove)

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
      currentSolution: Solution,
      availableCities: Set[Int],
      move: Move
  ): Unit = {
    move match {
      case EdgeSwap(edge1, edge2) =>
        val citiesInRemovedEdges =
          Set(edge1.city1, edge1.city2, edge2.city1, edge2.city2)

        improvingMoves = improvingMoves
          .filter {
            case (EdgeSwap(e1, e2), _) =>
              !(e1 == edge1 || e1 == edge2 || e2 == edge1 || e2 == edge2)
            case (NodeSwapOut(triplet, city), _) =>
              !tripletContainsAnyCity(triplet, citiesInRemovedEdges)
            case _ => true
          }

        val newMoves =
          getAllEdgeSwapsForEdge(
            currentSolution,
            Pair(edge1.city1, edge2.city1)
          ) ++ getAllEdgeSwapsForEdge(
            currentSolution,
            Pair(edge1.city2, edge2.city2)
          ) ++ getAllEdgeSwapsForEdge(
            currentSolution,
            Pair(edge2.city1, edge1.city1)
          ) ++ getAllEdgeSwapsForEdge(
            currentSolution,
            Pair(edge2.city2, edge1.city2)
          ) ++ getAllNodeSwapsOutForRemovedCities(
            currentSolution,
            citiesInRemovedEdges,
            availableCities
          )

        improvingMoves ++= newMoves
          .map(move => (move, getDeltaCost(move)))
          .filter(_._2 < 0)

      case NodeSwapOut(
            Triplet(cityBefore, removedCity, cityAfter),
            addedCity
          ) =>
        improvingMoves = improvingMoves
          .mapInPlace {
            case (move @ NodeSwapOut(_, `addedCity`), _) => {
              (move, 1) // move cant be applied anymore
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
                  case _ => triplet
                },
                city
              )
              (newMove, getDeltaCost(newMove))
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
              (newMove, getDeltaCost(newMove))
            }

            case move => move
          }
          .filter(_._2 < 0)

        val newMoves =
          getAllEdgeSwapsForEdge(
            currentSolution,
            Pair(cityBefore, addedCity)
          ) ++
            getAllEdgeSwapsForEdge(
              currentSolution,
              Pair(addedCity, cityAfter)
            ) ++
            getAllEdgeSwapsForEdge(
              currentSolution,
              Pair(addedCity, cityBefore)
            ) ++
            getAllEdgeSwapsForEdge(
              currentSolution,
              Pair(cityAfter, addedCity)
            ) ++
            getAllNodeSwapsForGivenCity(
              currentSolution,
              availableCities,
              cityBefore
            ) ++
            getAllNodeSwapsWithGivenCity(currentSolution, removedCity) ++
            getAllNodeSwapsForGivenCity(
              currentSolution,
              availableCities,
              cityAfter
            )

        improvingMoves ++= newMoves
          .map(move => (move, getDeltaCost(move)))
          .filter(_._2 < 0)

      case _ => ()
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
    val edges = getCycleConsecutivePairs(solution.path).toSet
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

  private def findFirstApplicableMove(
      condition: (Move) => Boolean
  ): Option[(Move, Int)] = {
    val removedMoves = mutable.ListBuffer[(Move, Int)]()
    var best =
      if (improvingMoves.nonEmpty) improvingMoves.dequeue()
      else (NodeSwapOut(Triplet(0, 0, 0), 0), 100)
    while (!condition(best._1) && improvingMoves.nonEmpty && best._2 < 0) {
      removedMoves += best
      best = improvingMoves.dequeue()
    }
    if (best._2 < 0 && condition(best._1)) {
      improvingMoves ++= removedMoves
      Some(best)
    } else {
      improvingMoves ++= removedMoves
      None
    }
  }

  private def getAllNodeSwapsOutForRemovedCities(
      solution: Solution,
      citiesFromRemovedEdges: Set[Int],
      availableCities: Set[Int]
  ): Seq[NodeSwapOut] = {
    val triplets = getCycleConsecutiveTriplets(solution.path).toSet
    triplets
      .filter(t =>
        citiesFromRemovedEdges.contains(t.city1) ||
          citiesFromRemovedEdges.contains(t.city2) ||
          citiesFromRemovedEdges.contains(t.city3)
      )
      .flatMap { triplet =>
        availableCities.map(city => NodeSwapOut(triplet, city))
      }
      .toList
  }

  private def getAllNodeSwapsWithGivenCity(
      solution: Solution,
      city: Int
  ): List[NodeSwapOut] = {
    if (solution.path.contains(city)) return List()
    else {
      val triplets = getCycleConsecutiveTriplets(solution.path)
      triplets
        .map(triplet => NodeSwapOut(triplet, city))
        .toList
    }
  }

  private def getAllNodeSwapsForGivenCity(
      solution: Solution,
      availableCities: Set[Int],
      city: Int
  ): List[NodeSwapOut] = {
    if (!solution.path.contains(city)) return List()
    else {
      val idx = solution.path.indexOf(city)
      val triplet = Triplet(
        solution.path(if (idx == 0) 99 else idx - 1),
        city,
        solution.path((idx + 1) % solution.path.length)
      )
      availableCities.map(c => NodeSwapOut(triplet, c)).toList
    }
  }
}

object ListOfImprovingMovesSolution {
  def apply(
      initialSolution: Solution,
      availableCities: Set[Int]
  ): ListOfImprovingMovesSolution = {
    val instance = new ListOfImprovingMovesSolution()
    instance.init(initialSolution, availableCities)
    instance
  }
}
