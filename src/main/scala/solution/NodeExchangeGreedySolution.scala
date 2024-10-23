import scala.annotation.tailrec
import scala.util.Random

object NodeExchangeGreedySolution {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    val extendedCycle = currentSolution.path ++ currentSolution.path.take(2)
    val iterator = extendedCycle.sliding(3).iterator

    while (iterator.hasNext) {
      val Seq(city1: Int, city2: Int, city3: Int) = iterator.next()
      for (cityToInsert <- availableCities) {
        val additionalCost = problemInstance.distances(city1)(cityToInsert) +
          problemInstance.cityCosts(cityToInsert) +
          problemInstance.distances(cityToInsert)(city3) -
          problemInstance.distances(city1)(city2) -
          problemInstance.cityCosts(city2) -
          problemInstance.distances(city2)(city3)

        if (additionalCost < 0) {
          val cityToRemove = city2
          val newCycle = currentSolution.path
            .patch(
              currentSolution.path.indexOf(cityToRemove),
              List(cityToInsert),
              1
            )
          return (
            Solution(
              newCycle,
              currentSolution.cost + additionalCost
            ),
            availableCities + city2 - cityToInsert
          )
        }
      }
    }

    (currentSolution, availableCities)
  }
}
