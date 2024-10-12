import scala.io.Source

object CSVReader {
  def readCSV(filePath: String): ProblemInstance = {
    val bufferedSource = Source.fromFile(filePath)
    val cities = bufferedSource
      .getLines()
      .map { line =>
        val Array(x, y, cost) = line.split(";").map(_.toInt)
        City(x, y, cost)
      }
      .toArray
    bufferedSource.close()
    val distances = for {
      city1 <- cities
      city2 <- cities
    } yield (city1, city2) -> (Cost.euclidean(city1, city2) + city2.cost)

    val expectedSolutionLen =
      if (cities.size % 2 == 1) (cities.size + 1) / 2 else cities.size / 2

    ProblemInstance(cities, distances.toMap, expectedSolutionLen)
  }
}
