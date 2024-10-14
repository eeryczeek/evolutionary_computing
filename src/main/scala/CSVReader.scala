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
          .map(city2 => (Cost.euclidean(city1, city2) + city2.cost))
          .toArray
      )
      .toArray

    ProblemInstance(cities.map(_.id).toSet, distances, (cities.size + 1) / 2)
  }
}
