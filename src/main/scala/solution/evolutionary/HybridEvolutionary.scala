import scala.collection.mutable.PriorityQueue

object HybridEvolutionary
    extends MoveGenerator
    with MoveOperations
    with CostManager {

  def search(
      recombinationOperator: (Solution, Solution) => Solution
  ): Solution = {
    val population = Population(70)
    var numOfIterations = 0
    println(
      s"Initial costs: ${population.solutions.map(_.cost).mkString(", ")}"
    )
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < 20000) {
      val parents = selectParents(population)
      println()
      println(s"Iteration: $numOfIterations")
      val child = recombinationOperator(parents._1, parents._2)
      println(s"Child cost: ${child.cost}")
      population.updatePopulationInPlace(child)
      numOfIterations += 1
      println(s"Best solution cost: ${population.best.cost}")
    }
    println(s"population costs: ${population.solutions.map(_.cost)}")

    population.best.copy(additionalData =
      Some(AdditionalData(Some(numOfIterations)))
    )
  }

  def searchRandom(): Solution = search(recombinationRandom)

  def searchHeuristic(): Solution = search(recombinationHeuristic)

  def recombinationRandom(
      parent1: Solution,
      parent2: Solution
  ): Solution = {
    val commonParts = RecombinationUtils.destroy2(parent1, parent2)
    // if (commonParts.find(part => part.toSet.size != part.size).isDefined) {
    //   println("Parts contain duplicate cities")
    //   commonParts.foreach(println)
    //   println()
    //   throw new Exception("Parts contain duplicate cities")
    // }
    //
    // if (commonParts.flatten.toSet.size != commonParts.flatten.size) {
    //   println("Common parts contain duplicate cities")
    //   commonParts.foreach(println)
    //   val duplicates = commonParts.flatten.diff(commonParts.flatten.toSet.toSeq)
    //   println(
    //     s"Duplicate cities: ${duplicates.mkString(", ")}"
    //   )
    //   duplicates.foreach { city =>
    //     println(
    //       s"City $city is present in ${commonParts.zipWithIndex.filter(_._1.contains(city)).map(_._2).mkString(", ")} parts"
    //     )
    //   }
    //   println()
    //   throw new Exception("Common parts contain duplicate cities")
    // }

    val mergedPath = RecombinationUtils.mergePartsRandomly(
      commonParts,
      ProblemInstanceHolder.problemInstance.cities -- commonParts.flatten
    )

    val intermidiateSolution = Solution(
      mergedPath,
      calculateSolutionCost(mergedPath)
    )

    SolutionModifier.getLocalSearchGreedy(intermidiateSolution)
  }

  def recombinationHeuristic(
      parent1: Solution,
      parent2: Solution
  ): Solution = {
    val commonParts = RecombinationUtils.destroy2(parent1, parent2)

    val mergedPath = RecombinationUtils.mergePartsHeuristically(
      commonParts,
      ProblemInstanceHolder.problemInstance.cities -- commonParts.flatten
    )

    Solution(
      mergedPath,
      calculateSolutionCost(mergedPath)
    )
  }

  private def selectParents(
      population: Population
  ): (Solution, Solution) = {
    val solutions = population.solutions.toVector
    val parent1 = solutions(scala.util.Random.nextInt(solutions.size))
    val parent2 = solutions(scala.util.Random.nextInt(solutions.size))
    (parent1, parent2)
  }
}

class Population(
    val solutions: PriorityQueue[Solution]
) {
  import Population._

  def updatePopulationInPlace(newSolution: Solution): Unit = {
    if (
      newSolution.cost < solutions.head.cost &&
      isUniqueSolution(newSolution, this)
    ) {
      solutions.dequeue()
      solutions.enqueue(newSolution)
    }
  }

  def best: Solution = solutions.min
}

object Population {
  implicit val ordering: Ordering[Solution] = Ordering.by(_.cost)

  def apply(size: Int): Population = {
    val initialPopulation = getInitialPopulation(size)
    new Population(PriorityQueue(initialPopulation: _*))
  }

  private def getInitialPopulation(size: Int): List[Solution] = {
    (0 to size).map { _ =>
      SolutionGenerator.generateRandomSolution()
    }.toList
  }

  private def isUniqueSolution(
      solution: Solution,
      population: Population
  ): Boolean = {
    !population.solutions.exists(_.path == solution.path)
  }
}
