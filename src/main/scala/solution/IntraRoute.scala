import scala.io.Source
import com.typesafe.scalalogging.Logger
trait IntraRoute {
  private val logger = Logger("IntraRoute")
  case class Edge(city1: Int, city2: Int, city1Position: Int)
  sealed trait Move
  case class NodeSwap(
      city1: Int,
      city1Position: Int,
      city2: Int,
      city2Position: Int
  ) extends Move
  case class EdgeSwap(edge1: Edge, edge2: Edge) extends Move

  def findPossibleMoves(
      solution: Solution,
      expectedPathLen: Int = 200
  ): Seq[Move] = {
    val path = solution.path.zipWithIndex
    val nodeSwaps: Seq[Move] = path.tails.flatMap {
      case Nil => Seq()
      case (city1, position1) :: rest =>
        rest.map { case (city2, position2) =>
          NodeSwap(city1, position1, city2, position2)
        }
    }.toSeq

    val edges = (path :+ path.head)
      .sliding(2)
      .filter(_.size == 2)
      .map { case (city1, position) :: (city2, _) :: Nil =>
        Edge(city1, city2, position)
      }
      .toSeq

    val edgeSwaps: Seq[Move] = edges.tails.flatMap {
      case list if list.size < 2 => Seq() // adjecent edges can't be swapped
      case edge1 :: _ :: edge2 :: Nil => Seq(EdgeSwap(edge1, edge2))
      case edge1 :: rest if rest.size == expectedPathLen - 1 =>
        rest.tail.take(expectedPathLen - 2).map(edge2 => EdgeSwap(edge1, edge2))
      case edge1 :: rest => rest.tail.map(edge2 => EdgeSwap(edge1, edge2))
    }.toSeq
    nodeSwaps ++ edgeSwaps
  }

  def getAdditionalCost(
      problemInstance: ProblemInstance,
      solution: Solution,
      move: Move
  ): Int = {
    val cityAt: Int => Int = readPathInAsCycle(solution.path)
    val distances = problemInstance.distances
    val path = solution.path.last +: solution.path :+ solution.path.head

    move match {
      case EdgeSwap(edge1, edge2) =>
        distances(edge1.city1)(edge2.city1) +
          distances(edge1.city2)(edge2.city2) -
          distances(edge1.city1)(edge1.city2) -
          distances(edge2.city1)(edge2.city2)

      case NodeSwap(city1, city1Position, city2, city2Position) =>
        if (city1Position == 0 && city2Position == solution.path.size - 1) {
          distances(cityAt(city2Position - 1))(city1) +
            distances(city2)(cityAt(city1Position + 1)) -
            distances(cityAt(city2Position - 1))(city2) -
            distances(city1)(cityAt(city1Position + 1))
        } else if (math.abs(city1Position - city2Position) == 1) {
          distances(cityAt(city1Position - 1))(city2) +
            distances(city1)(cityAt(city2Position + 1)) -
            distances(cityAt(city1Position - 1))(city1) -
            distances(city2)(cityAt(city2Position + 1))
        } else {
          distances(cityAt(city1Position - 1))(city2) +
            distances(city2)(cityAt(city1Position + 1)) +
            distances(cityAt(city2Position - 1))(city1) +
            distances(city1)(cityAt(city2Position + 1)) -
            distances(cityAt(city1Position - 1))(city1) -
            distances(city1)(cityAt(city1Position + 1)) -
            distances(cityAt(city2Position - 1))(city2) -
            distances(city2)(cityAt(city2Position + 1))
        }
    }
  }

  def readPathInAsCycle(path: Seq[Int])(idx: Int): Int = {
    val adjustedIdx =
      if (idx < 0)
        (path.size + (idx % path.size)) % path.size
      else idx % path.size
    path(adjustedIdx)
  }

  def updateSolutionWithMove(
      solution: Solution,
      move: Move,
      additionalCost: Int
  ): Solution = {
    val newPath = move match {
      case EdgeSwap(Edge(_, _, city1Position), Edge(_, _, city2Position)) =>
        if (city1Position < city2Position) {
          val sublistToReverse =
            solution.path.slice(city1Position + 1, city2Position + 1)
          solution.path.patch(
            city1Position + 1,
            sublistToReverse.reverse,
            sublistToReverse.length
          )
        } else {
          // Handle edge case where city1Position > city2Position (wrap around)
          val sublistToReverse = (solution.path.drop(
            city1Position + 1
          ) ++ solution.path.take(city2Position + 1))
          val reversedSegment = sublistToReverse.reverse
          val updatedPath =
            solution.path.take(city1Position + 1) ++ reversedSegment.dropRight(
              city2Position + 1
            ) ++ reversedSegment.takeRight(city2Position + 1)
          updatedPath
        }
      case NodeSwap(city1, city1Position, city2, city2Position) =>
        solution.path
          .updated(city1Position, city2)
          .updated(city2Position, city1)
    }

    Solution(path = newPath, cost = solution.cost + additionalCost)
  }
}
