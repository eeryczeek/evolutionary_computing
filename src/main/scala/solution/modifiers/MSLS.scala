object MSLS {
  def run(): Solution = {
    val results = for (i <- 1 to 200) yield {
      SolutionModifier.getLocalSearchWithListOfImprovingMoves(() =>
        SolutionGenerator.generateRandomSolution()
      )
    }
    results.minBy(_.cost)
  }
}
