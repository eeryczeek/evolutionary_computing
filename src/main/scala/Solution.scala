import scala.util.Random
import scala.util.control.TailCalls.TailRec
import scala.annotation.tailrec

trait Solution
case class PartrialSolution(path: List[City], cost: Int) extends Solution
case class FullSolution(path: List[City], cost: Int) extends Solution
case class FaultySolution(path: List[City], cost: Int, reason: String)
    extends Solution

object Cost {
  def euclidean(city1: City, city2: City): Int = {
    math.round(
      math
        .sqrt(math.pow(city1.x - city2.x, 2) + math.pow(city1.y - city2.y, 2))
        .asInstanceOf[Float]
    )
  }
}

object SolutionFactory {
  type Distances = Map[(City, City), Int]

  def getRandomSolution(
      problemInstance: ProblemInstance
  ): Either[FaultySolution, FullSolution] = {
    generate(
      problemInstance,
      problemInstance.cities,
      (_, cities, _) => {
        val randomIdx = Random.nextInt(cities.size - 1)
        cities.drop(randomIdx).take(1).toList.headOption
      },
      PartrialSolution(List.empty, cost = 0)
    )
  }

  def getGreedySolution(
      problemInstance: ProblemInstance
  ): Either[FaultySolution, FullSolution] = {
    val initialCity = selectRandomCity(problemInstance.cities)
    initialCity match {
      case None =>
        Left(FaultySolution(List.empty, 0, "Can't find initial city"))
      case Some(city) =>
        generate(
          problemInstance,
          problemInstance.cities.filter(_ != city),
          selectGreedyCity,
          PartrialSolution(List(city), 0)
        )
    }
  }

  @tailrec
  def generate(
      problemInstance: ProblemInstance,
      citiesToChooseFrom: Iterable[City],
      nextCitySelector: (
          ProblemInstance,
          Iterable[City],
          PartrialSolution
      ) => Option[City],
      currentSolution: PartrialSolution
  ): Either[FaultySolution, FullSolution] = {
    val choosenCity =
      nextCitySelector(problemInstance, citiesToChooseFrom, currentSolution)

    if (choosenCity.isEmpty) {
      Left(
        FaultySolution(
          currentSolution.path,
          currentSolution.cost,
          "Can't find next city"
        )
      )
    } else {
      val lastCity = currentSolution.path.lastOption
      val additionalCost = lastCity match {
        case None => 0
        case Some(city) =>
          problemInstance.distances.get((city, choosenCity.get)).getOrElse(0)
      }
      val remainingCities = citiesToChooseFrom.filter(_ != choosenCity.get)

      val newSolution =
        currentSolution.copy(
          path = currentSolution.path :+ choosenCity.get,
          cost = currentSolution.cost + additionalCost
        )

      if (newSolution.path.size == problemInstance.expectedSolutionLen) {
        val distanceFromLastToFirstCity = problemInstance.distances(
          (newSolution.path.head, newSolution.path.last)
        )
        Right(
          FullSolution(
            path = newSolution.path,
            cost = newSolution.cost + distanceFromLastToFirstCity
          )
        )
      } else
        generate(
          problemInstance,
          remainingCities,
          nextCitySelector,
          newSolution
        )
    }
  }

  def selectRandomCity(cities: Iterable[City]): Option[City] = {
    val randomIdx = Random.nextInt(cities.size - 1)
    cities.drop(randomIdx).take(1).toList.headOption
  }

  def selectGreedyCity(
      problemInstance: ProblemInstance,
      cities: Iterable[City],
      solution: PartrialSolution
  ): Option[City] = {
    val lastVisitedCity = solution.path.last
    cities
      .map(city => city -> problemInstance.distances((lastVisitedCity, city)))
      .maxByOption(_._2)
      .map(_._1)
  }
}
