case class Pair(city1: Int, city2: Int)
case class Triplet(city1: Int, city2: Int, city3: Int)

sealed trait Move
case class AppendAtEnd(last: Int, city: Int) extends Move
case class PrependAtStart(first: Int, city: Int) extends Move
case class InsertBetween(pair: Pair, city: Int) extends Move
case class EdgeSwap(edge1: Pair, edge2: Pair) extends Move
case class NodeSwapIn(triplet1: Triplet, triplet2: Triplet) extends Move
case class NodeSwapOut(triplet: Triplet, city: Int) extends Move
case class TwoNodeExchange(triplet: Triplet, pair: Pair, city: Int) extends Move

trait MoveOperations {
  def performMove(
      currentSolution: Solution,
      move: Move,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    move match {
      case appendAtEnd: AppendAtEnd =>
        performAppendAtEnd(
          currentSolution,
          appendAtEnd,
          availableCities
        )
      case prependAtStart: PrependAtStart =>
        performPrependAtStart(
          currentSolution,
          prependAtStart,
          availableCities
        )
      case insertBetween: InsertBetween =>
        performInsertBetween(
          currentSolution,
          insertBetween,
          availableCities
        )
      case edgeSwap: EdgeSwap =>
        performEdgeSwap(currentSolution, edgeSwap, availableCities)
      case nodeSwapIn: NodeSwapIn =>
        performNodeSwapIn(
          currentSolution,
          nodeSwapIn,
          availableCities
        )
      case nodeSwapOut: NodeSwapOut =>
        performNodeSwapOut(
          currentSolution,
          nodeSwapOut,
          availableCities
        )
      case twoNodeExchange: TwoNodeExchange =>
        performTwoNodeExchange(
          currentSolution,
          twoNodeExchange,
          availableCities
        )
    }
  }

  def getConsecutivePairs(path: Seq[Int]): Seq[Pair] = {
    (path :+ path.head).sliding(2).map { case Seq(a, b) => Pair(a, b) }.toSeq
  }

  def getConsecutiveTriplets(path: Seq[Int]): Seq[Triplet] = {
    (path ++ path
      .take(2)).sliding(3).map { case Seq(a, b, c) => Triplet(a, b, c) }.toSeq
  }

  private def performAppendAtEnd(
      currentSolution: Solution,
      appendAtEnd: AppendAtEnd,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val newPath = currentSolution.path :+ appendAtEnd.city
    val newAvailableCities = availableCities - appendAtEnd.city
    (currentSolution.copy(path = newPath), newAvailableCities)
  }

  private def performPrependAtStart(
      currentSolution: Solution,
      prependAtStart: PrependAtStart,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val newPath = prependAtStart.city +: currentSolution.path
    val newAvailableCities = availableCities - prependAtStart.city
    (currentSolution.copy(path = newPath), newAvailableCities)
  }

  private def performInsertBetween(
      currentSolution: Solution,
      insertBetween: InsertBetween,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val InsertBetween(Pair(a, b), city) = insertBetween
    val city1Position = currentSolution.path.indexOf(a)
    val city2Position = currentSolution.path.indexOf(b)
    val newPath = currentSolution.path.patch(city1Position + 1, Seq(city), 0)

    val newAvailableCities = availableCities - city
    (currentSolution.copy(path = newPath), newAvailableCities)
  }

  private def performEdgeSwap(
      currentSolution: Solution,
      edgeSwap: EdgeSwap,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val EdgeSwap(Pair(a1, _), Pair(a2, _)) = edgeSwap
    val city1Position = currentSolution.path.indexOf(a1)
    val city2Position = currentSolution.path.indexOf(a2)

    val (start, end) = if (city1Position < city2Position) {
      (city1Position + 1, city2Position + 1)
    } else {
      (city2Position + 1, city1Position + 1)
    }

    val sublistToReverse = currentSolution.path.slice(start, end)
    val newPath = currentSolution.path.patch(
      start,
      sublistToReverse.reverse,
      sublistToReverse.length
    )

    (currentSolution.copy(path = newPath), availableCities)
  }

  private def performNodeSwapIn(
      currentSolution: Solution,
      nodeSwapIn: NodeSwapIn,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val NodeSwapIn(Triplet(_, b1, _), Triplet(_, b2, _)) = nodeSwapIn
    val city2index = currentSolution.path.indexOf(b1)
    val city5index = currentSolution.path.indexOf(b2)
    val newPath = currentSolution.path
      .updated(city2index, b2)
      .updated(city5index, b1)

    (currentSolution.copy(path = newPath), availableCities)
  }

  private def performNodeSwapOut(
      currentSolution: Solution,
      nodeSwapOut: NodeSwapOut,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val NodeSwapOut(Triplet(_, b, _), city) = nodeSwapOut
    val newPath = currentSolution.path
      .updated(currentSolution.path.indexOf(b), city)

    val newAvailableCities = availableCities + b - city
    (currentSolution.copy(path = newPath), newAvailableCities)
  }

  private def performTwoNodeExchange(
      currentSolution: Solution,
      twoNodeExchange: TwoNodeExchange,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val TwoNodeExchange(Triplet(a1, b1, c1), Pair(a2, b2), city) =
      twoNodeExchange
    val updatedPath = currentSolution.path.filter(_ != b1)
    val a2index = updatedPath.indexOf(a2)
    val newPath =
      updatedPath.take(a2index + 1) ++
        List(city) ++
        updatedPath.drop(a2index + 1)
    val newAvailableCities = availableCities + b1 - city
    assert(
      newPath.distinct.size == currentSolution.path.distinct.size,
      s"$twoNodeExchange\n${currentSolution.path.mkString(" ")} -> ${newPath.mkString(" ")}"
    )
    (currentSolution.copy(path = newPath), newAvailableCities)
  }
}
