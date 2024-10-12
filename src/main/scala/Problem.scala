case class ProblemInstance(
    cities: Array[City],
    distances: Map[(City, City), Int],
    expectedSolutionLen: Int
)
case class City(x: Int, y: Int, cost: Int)
