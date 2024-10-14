import scala.annotation.tailrec

object GreedyAtAnyPositionSolution {
  @tailrec
  def generate(
      problemInstance: ProblemInstance,
      currentSolution: PartialSolution,
      citiesToChooseFrom: Set[Int]
  ): FullSolution = {
    if (currentSolution.path.size == problemInstance.expectedSolutionLen) {
      FullSolution(
        currentSolution.path,
        currentSolution.cost + problemInstance.distances(
          currentSolution.path.last
        )(currentSolution.path.head)
      )
    } else {
      val (newPartialSolution, newCitiesToChooseFrom) = updateSolution(
        problemInstance,
        currentSolution,
        citiesToChooseFrom
      )
      generate(
        problemInstance,
        newPartialSolution,
        newCitiesToChooseFrom
      )
    }
  }

  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: PartialSolution,
      citiesToChooseFrom: Set[Int]
  ): (PartialSolution, Set[Int]) = {
    val path = currentSolution.path
    val distances = problemInstance.distances
    val pathIndices = path.zipWithIndex.toMap

    val (bestCity, bestCost, insertPosition) = citiesToChooseFrom.view
      .flatMap { city =>
        val prependCost = distances(city)(path.head)
        val appendCost = distances(path.last)(city)
        val insertCosts = path
          .zip(path.tail)
          .view
          .map { case (city1, city2) =>
            (
              city,
              distances(city1)(city) +
                distances(city)(city2) -
                distances(city1)(city2),
              pathIndices(city1)
            )
          }

        val bestInsert =
          if (insertCosts.isEmpty) (city, prependCost, -1)
          else insertCosts.minBy(_._2)

        Seq(
          (city, prependCost, -1),
          (city, appendCost, path.size),
          bestInsert
        )
      }
      .minBy(_._2)

    val newPath = insertPosition match {
      case -1                      => bestCity +: path
      case pos if pos == path.size => path :+ bestCity
      case pos => path.take(pos + 1) ++ Array(bestCity) ++ path.drop(pos + 1)
    }

    val newPartialSolution = PartialSolution(
      newPath,
      currentSolution.cost + bestCost
    )

    (newPartialSolution, citiesToChooseFrom - bestCity)
  }
}
