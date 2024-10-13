case class ProblemInstance(
    cities: Array[City],
    distances: Array[Array[Int]],
    expectedSolutionLen: Int
)
case class City(id: Int, x: Int, y: Int, cost: Int)
