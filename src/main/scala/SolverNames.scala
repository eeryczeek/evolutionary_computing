object SolverNames extends Enumeration {
  val RandomSolution = Value("RandomSolution")
  val TailAppendSolution = Value("TailAppendSolution")
  val InsertAnyPositionSolution = Value("InsertAnyPositionSolution")
  val CycleSolution = Value("CycleSolution")
  val CycleRegretSolution = Value("CycleRegretSolution")
  val CycleWeightedRegretSolution = Value("CycleWeightedRegretSolution")

  val GreedyLocalSearch = Value("GreedyLocalSearch")
  val SteepestLocalSearch = Value("SteepestLocalSearch")
  val ListOfImprovingMoves = Value("ListOfImprovingMoves")

  val MSLS = Value("MSLS")
  val ILS = Value("IteratedLocalSearch")
  val LNSWithLS = Value("LargeNeighborhoodSearchWithLS")
  val LNSWithoutLS = Value("LargeNeighborhoodSearchWithoutLS")

  val advancedHeuristics = List(
    LNSWithLS,
    LNSWithoutLS,
    ILS,
    MSLS
  )
}
