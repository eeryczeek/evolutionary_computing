object MSLS {
  def run(): Solution = {
    val results = for (i <- 1 to 200) yield {
      SolutionFactory.getLocalSearchWithListOfImprovingMoves(() =>
        SolutionFactory.getRandomSolution()
      )
    }
    results.minBy(_.cost)
  }
}
