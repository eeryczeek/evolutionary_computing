import scala.annotation.tailrec
import scala.util.Random

object NodeExchangeSteepestSolution {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val extendedCycle = currentSolution.path ++ currentSolution.path.take(2)
    val improvements = extendedCycle
      .sliding(3)
      .flatMap { case Seq(city1: Int, city2: Int, city3: Int) =>
        availableCities.view.map { city =>
          (
            city2,
            city,
            problemInstance.distances(city1)(city) +
              problemInstance.distances(city)(city3) +
              problemInstance.cityCosts(city) -
              problemInstance.distances(city1)(city2) -
              problemInstance.distances(city2)(city3) -
              problemInstance.cityCosts(city2)
          )
        }
      }
      .filter(_._3 < 0)
      .toList

    improvements match {
      case Nil => (currentSolution, availableCities)
      case _ =>
        val (cityToRemove, cityToInsert, additionalCost) =
          improvements.minBy(_._3)
        val newCycle = currentSolution.path
          .patch(
            currentSolution.path.indexOf(cityToRemove),
            List(cityToInsert),
            1
          )
        (
          Solution(
            newCycle,
            currentSolution.cost + additionalCost
          ),
          availableCities + cityToRemove - cityToInsert
        )
    }
  }
}
