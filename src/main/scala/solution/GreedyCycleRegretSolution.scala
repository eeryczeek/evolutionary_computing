import scala.annotation.tailrec

object GreedyCycleRegretSolution {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: PartialSolution,
      availableCities: Set[Int]
  ): (PartialSolution, Set[Int]) = {
    val currentCycle = currentSolution.path
    val distances = problemInstance.distances

    val (cityToInsert, insertIndex, additionalCost, _) = availableCities.view
      .flatMap { city =>
        currentCycle
          .zip(currentCycle.tail :+ currentCycle.head)
          .zipWithIndex
          .map { case ((city1, city2), i) =>
            val insertionCost = distances(city1)(city) + distances(city)(
              city2
            ) - distances(city1)(city2)
            (city, i, insertionCost)
          }
      }
      .groupBy(_._1)
      .mapValues(_.toList.sortBy(_._3).take(2))
      .map { case (city, costs) =>
        val regret =
          if (costs.size == 2) costs(1)._3 - costs(0)._3 else costs.head._3
        (city, costs.head._2, costs.head._3, regret)
      }
      .maxBy(_._4)

    val newCycle = currentCycle.take(insertIndex + 1) ++ List(
      cityToInsert
    ) ++ currentCycle.drop(insertIndex + 1)
    (
      PartialSolution(
        newCycle,
        currentSolution.cost + additionalCost
      ),
      availableCities - cityToInsert
    )
  }
}
