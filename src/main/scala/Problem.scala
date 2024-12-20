case class ProblemInstance(
    cities: Set[Int],
    distances: Array[Array[Int]],
    cityCosts: Array[Int],
    candidateEdges: Array[Set[Int]],
    expectedSolutionLen: Int
)
case class City(id: Int, x: Int, y: Int, cost: Int)

object Cost {
  def euclidean(city1: City, city2: City): Int = {
    math.round(
      math
        .sqrt(math.pow(city1.x - city2.x, 2) + math.pow(city1.y - city2.y, 2))
        .toFloat
    )
  }

  def calculateSolutionCost(solution: Solution): Int = {
    val distances = ProblemInstanceHolder.problemInstance.distances
    val costs = ProblemInstanceHolder.problemInstance.cityCosts
    val path = solution.path

    path
      .zip(path.tail :+ path.head)
      .map { case (city1, city2) => costs(city1) + distances(city1)(city2) }
      .sum
  }
}
