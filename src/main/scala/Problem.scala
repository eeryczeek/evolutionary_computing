case class ProblemInstance(
    cities: Set[Int],
    distances: Array[Array[Int]],
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
}
