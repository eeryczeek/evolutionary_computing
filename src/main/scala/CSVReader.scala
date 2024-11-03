import scala.io.Source

object CSVReader {
  def readCSV(filePath: String): ProblemInstance = {
    val bufferedSource = Source.fromFile(filePath)
    val cities = bufferedSource
      .getLines()
      .zipWithIndex
      .map { lineWithId =>
        val (line, id) = lineWithId
        val Array(x, y, cost) = line.split(";").map(_.toInt)
        City(id, x, y, cost)
      }
      .toArray
    bufferedSource.close()
    val sortedCities = cities.sortBy(_.id)
    val distances = sortedCities
      .map(city1 =>
        sortedCities
          .map(city2 => (Cost.euclidean(city1, city2)))
          .toArray
      )
      .toArray
    val cityCosts = sortedCities.map(_.cost).toArray

    val candidateEdges = distances
      .map(_.zipWithIndex)
      .zipWithIndex
      .map { case (distancesWithIndex, city1) =>
        distancesWithIndex
          .filter { case (_, city2) => city1 != city2 }
          .sortBy { case (distance, city2) => distance + cityCosts(city2) }
          .take(10)
          .map { case (_, city2) => city2 }
          .toSet
      }
    val expectedNumberOfCities = (cities.size + 1) / 2
    ProblemInstance(
      cities.map(_.id).toSet,
      distances,
      cityCosts,
      candidateEdges,
      expectedNumberOfCities
    )
  }
}
