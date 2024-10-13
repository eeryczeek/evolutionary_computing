import scala.util.Random
import scala.util.control.TailCalls.TailRec
import scala.annotation.tailrec

trait Solution
case class PartrialSolution(
    path: List[City],
    visitedCities: Set[City],
    cost: Int
) extends Solution
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
      PartrialSolution(List.empty, Set.empty, cost = 0)
    )
  }

  def getGreedyAppendSolution(
      problemInstance: ProblemInstance,
      initialCity: City
  ): Either[FaultySolution, FullSolution] = {
    generate(
      problemInstance,
      problemInstance.cities.filter(_ != initialCity),
      selectGreedyCity,
      PartrialSolution(List(initialCity), Set(initialCity), 0)
    )
  }

  def getGreedyAnyPositionSolution(
      problemInstance: ProblemInstance,
      initialCity: City
  ): Either[FaultySolution, FullSolution] = ???

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
          problemInstance.distances(city.cityId)(choosenCity.get.cityId)
      }
      val remainingCities = citiesToChooseFrom.filter(_ != choosenCity.get)

      val newSolution =
        currentSolution.copy(
          path = currentSolution.path :+ choosenCity.get,
          visitedCities = currentSolution.visitedCities ++ choosenCity,
          cost = currentSolution.cost + additionalCost
        )

      if (newSolution.path.size == problemInstance.expectedSolutionLen) {
        val distanceFromLastToFirstCity = problemInstance.distances(
          newSolution.path.last.cityId
        )(newSolution.path.head.cityId)
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
      .map(city =>
        city -> problemInstance.distances(lastVisitedCity.cityId)(city.cityId)
      )
      .maxByOption(_._2)
      .map(_._1)
  }
}