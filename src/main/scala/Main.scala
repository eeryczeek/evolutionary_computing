import scala.util.control.TailCalls.TailRec
import scala.util.Random
import scala.io.Source

object Main extends App {
  def main(): Unit = {
    val initialData = CSVReader.readCSV("TSPA.csv")
    val randomSolution = RandomSolution(initialData)
    val result = randomSolution.cost

    result
  }
}

object CSVReader {
  def readCSV(filePath: String): InitialData = {
    val bufferedSource = Source.fromFile(filePath)
    val cities = bufferedSource
      .getLines()
      .map { line =>
        val Array(x, y, cost) = line.split(";").map(_.toInt)
        City(x, y, cost)
      }
      .toSet
    bufferedSource.close()

    InitialData(cities)
  }
}

object RandomSolution {
  def apply(initData: InitialData): Solution = {
    generate(initData.cities, Solution(List()))
  }

  def generate(
      citiesToChooseFrom: Set[City],
      currentSolution: Solution
  ): Solution = {
    val selectedIdx = Random.nextInt(citiesToChooseFrom.size)
    val choosenCity = citiesToChooseFrom.iterator.drop(selectedIdx).next
    val newSolution =
      currentSolution.copy(cities = choosenCity :: currentSolution.cities)
    val filteredCities = citiesToChooseFrom.filter(_ != choosenCity)

    if (citiesToChooseFrom.size == 0) newSolution
    else generate(filteredCities, newSolution)
  }
}

object Cost {
  def euclidean(city1: City, city2: City): Double = {
    math.sqrt(math.pow(city1.x - city2.x, 2) + math.pow(city1.y - city2.y, 2))
  }
}

case class InitialData(cities: Set[City])
case class City(x: Int, y: Int, cost: Int)
case class Solution(cities: List[City]) {
  def cost: Double = {
    var lastCity = cities.head
    var cost: Double = lastCity.cost
    for (city <- cities.tail) {
      cost += city.cost
      cost += Cost.euclidean(lastCity, city)
      lastCity = city
    }
    cost += Cost.euclidean(lastCity, cities.head)

    cost
  }
}
