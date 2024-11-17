trait CostManager {
  def getSolutionCost(
      problemInstance: ProblemInstance,
      solution: Solution
  ): Int = {
    val distances = problemInstance.distances
    val cityCosts = problemInstance.cityCosts
    (solution.path :+ solution.path.head)
      .sliding(2)
      .map { case Array(a, b) =>
        distances(a)(b) + cityCosts(b)
      }
      .sum
  }

  def getDeltaCost(
      problemInstance: ProblemInstance,
      move: Move
  ): Int = {
    move match {
      case appendAtEnd: AppendAtEnd =>
        getAppendAtEndCost(problemInstance, appendAtEnd)
      case prependAtStart: PrependAtStart =>
        getPrependAtStartCost(problemInstance, prependAtStart)
      case insertBetween: InsertBetween =>
        getInsertBetweenCost(problemInstance, insertBetween)
      case edgeSwap: EdgeSwap =>
        getEdgeSwapCost(problemInstance, edgeSwap)
      case nodeSwapIn: NodeSwapIn =>
        getNodeSwapInCost(problemInstance, nodeSwapIn)
      case nodeSwapOut: NodeSwapOut =>
        getNodeSwapOutCost(problemInstance, nodeSwapOut)
    }
  }

  private def getAppendAtEndCost(
      problemInstance: ProblemInstance,
      move: AppendAtEnd
  ): Int = {
    val distances = problemInstance.distances
    val cityCosts = problemInstance.cityCosts
    val AppendAtEnd(last, city) = move
    distances(last)(city) + cityCosts(city)
  }

  private def getPrependAtStartCost(
      problemInstance: ProblemInstance,
      move: PrependAtStart
  ): Int = {
    val distances = problemInstance.distances
    val cityCosts = problemInstance.cityCosts
    val PrependAtStart(first, city) = move
    distances(city)(first) + cityCosts(city)
  }

  private def getInsertBetweenCost(
      problemInstance: ProblemInstance,
      move: InsertBetween
  ): Int = {
    val distances = problemInstance.distances
    val cityCosts = problemInstance.cityCosts
    val InsertBetween(Pair(a, b), city) = move
    distances(a)(city) +
      distances(city)(b) -
      distances(a)(b) +
      cityCosts(city)
  }

  private def getEdgeSwapCost(
      problemInstance: ProblemInstance,
      move: EdgeSwap
  ): Int = {
    val distances = problemInstance.distances
    val cityCosts = problemInstance.cityCosts
    val EdgeSwap(Pair(a1, b1), Pair(a2, b2)) = move
    distances(a1)(a2) +
      distances(b1)(b2) -
      distances(a1)(b1) -
      distances(a2)(b2)
  }

  private def getNodeSwapInCost(
      problemInstance: ProblemInstance,
      move: NodeSwapIn
  ): Int = {
    val distances = problemInstance.distances
    val cityCosts = problemInstance.cityCosts
    val NodeSwapIn(Triplet(a1, b1, c1), Triplet(a2, b2, c2)) = move
    if ((a1, b1) == (b2, c2)) {
      distances(a2)(c2) +
        distances(b2)(c1) -
        distances(a2)(b2) -
        distances(b1)(c1)
    } else if ((b1, c1) == (a2, b2)) {
      distances(a1)(c1) +
        distances(b1)(c2) -
        distances(a1)(b1) -
        distances(b2)(c2)
    } else {
      distances(a1)(b2) +
        distances(b2)(c1) +
        distances(a2)(b1) +
        distances(b1)(c2) -
        distances(a1)(b1) -
        distances(b1)(c1) -
        distances(a2)(b2) -
        distances(b2)(c2)
    }
  }

  private def getNodeSwapOutCost(
      problemInstance: ProblemInstance,
      move: NodeSwapOut
  ): Int = {
    val distances = problemInstance.distances
    val cityCosts = problemInstance.cityCosts
    val NodeSwapOut(Triplet(a, b, c), city) = move
    distances(a)(city) +
      distances(city)(c) +
      cityCosts(city) -
      distances(a)(b) -
      distances(b)(c) -
      cityCosts(b)
  }
}
