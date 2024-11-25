trait CostManager {
  def getSolutionCost(
      solution: Solution
  ): Int = {
    val distances = ProblemInstanceHolder.problemInstance.distances
    val cityCosts = ProblemInstanceHolder.problemInstance.cityCosts
    (solution.path :+ solution.path.head)
      .sliding(2)
      .map { case Array(a, b) => distances(a)(b) + cityCosts(b) }
      .sum
  }

  def getDeltaCost(move: Move): Int = {
    move match {
      case appendAtEnd: AppendAtEnd =>
        getAppendAtEndCost(appendAtEnd)
      case prependAtStart: PrependAtStart =>
        getPrependAtStartCost(prependAtStart)
      case insertBetween: InsertBetween =>
        getInsertBetweenCost(insertBetween)
      case edgeSwap: EdgeSwap =>
        getEdgeSwapCost(edgeSwap)
      case nodeSwapIn: NodeSwapIn =>
        getNodeSwapInCost(nodeSwapIn)
      case nodeSwapOut: NodeSwapOut =>
        getNodeSwapOutCost(nodeSwapOut)
      case twoNodeExchange: TwoNodeExchange =>
        getTwoNodeExchangeCost(twoNodeExchange)
    }
  }

  private def getAppendAtEndCost(move: AppendAtEnd): Int = {
    val distances = ProblemInstanceHolder.problemInstance.distances
    val cityCosts = ProblemInstanceHolder.problemInstance.cityCosts
    val AppendAtEnd(last, city) = move
    distances(last)(city) + cityCosts(city)
  }

  private def getPrependAtStartCost(move: PrependAtStart): Int = {
    val distances = ProblemInstanceHolder.problemInstance.distances
    val cityCosts = ProblemInstanceHolder.problemInstance.cityCosts
    val PrependAtStart(first, city) = move
    distances(city)(first) + cityCosts(city)
  }

  private def getInsertBetweenCost(move: InsertBetween): Int = {
    val distances = ProblemInstanceHolder.problemInstance.distances
    val cityCosts = ProblemInstanceHolder.problemInstance.cityCosts
    val InsertBetween(Pair(a, b), city) = move
    distances(a)(city) +
      distances(city)(b) -
      distances(a)(b) +
      cityCosts(city)
  }

  private def getEdgeSwapCost(move: EdgeSwap): Int = {
    val distances = ProblemInstanceHolder.problemInstance.distances
    val EdgeSwap(Pair(a1, b1), Pair(a2, b2)) = move
    distances(a1)(a2) +
      distances(b1)(b2) -
      distances(a1)(b1) -
      distances(a2)(b2)
  }

  private def getNodeSwapInCost(move: NodeSwapIn): Int = {
    val distances = ProblemInstanceHolder.problemInstance.distances
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

  private def getNodeSwapOutCost(move: NodeSwapOut): Int = {
    val distances = ProblemInstanceHolder.problemInstance.distances
    val cityCosts = ProblemInstanceHolder.problemInstance.cityCosts
    val NodeSwapOut(Triplet(a, b, c), city) = move
    distances(a)(city) +
      distances(city)(c) +
      cityCosts(city) -
      distances(a)(b) -
      distances(b)(c) -
      cityCosts(b)
  }

  private def getTwoNodeExchangeCost(move: TwoNodeExchange): Int = {
    val distances = ProblemInstanceHolder.problemInstance.distances
    val cityCosts = ProblemInstanceHolder.problemInstance.cityCosts
    val TwoNodeExchange(Triplet(a1, b1, c1), Pair(a2, b2), city) = move
    distances(a1)(c1) +
      distances(a2)(city) +
      distances(city)(b2) +
      cityCosts(city) -
      distances(a1)(b1) -
      distances(b1)(c1) -
      distances(a2)(b2) -
      cityCosts(b1)
  }
}
