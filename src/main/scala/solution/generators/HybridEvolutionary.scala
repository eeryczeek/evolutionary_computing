import scala.collection.mutable.PriorityQueue

object HybridEvolutionary
    extends MoveGenerator
    with MoveOperations
    with CostManager {

  def generateSolution(
      recombinationOperator: (Solution, Solution) => Solution
  ): Solution = {
    val startTime = System.currentTimeMillis()
    val population = Population(20)
    var numOfIterations = 0
    while (System.currentTimeMillis() - startTime < 22150) {
      val parents = selectParents(population)
      val child = recombinationOperator(parents._1, parents._2)
      population.updatePopulationInPlace(child)
      numOfIterations += 1
    }

    population.best
  }

  def recombinationRandom(
      parent1: Solution,
      parent2: Solution
  ): Solution = {
    val commonParts = DestructionUtils.destroy(parent1, parent2)

    val mergedPath = DestructionUtils.mergePartsRandomly(
      commonParts,
      ProblemInstanceHolder.problemInstance.cities -- commonParts.flatten
    )

    Solution(
      mergedPath,
      calculateSolutionCost(mergedPath)
    )
  }

  def recombinationHeuristic(
      parent1: Solution,
      parent2: Solution
  ): Solution = {
    val commonParts = DestructionUtils.destroy(parent1, parent2)

    val mergedPath = DestructionUtils.mergePartsHeuristically(
      commonParts,
      ProblemInstanceHolder.problemInstance.cities -- commonParts.flatten
    )

    Solution(
      mergedPath,
      calculateSolutionCost(mergedPath)
    )
  }

  private def getInitialPopulation(size: Int): List[Solution] = {
    (1 to size).map { _ =>
      RandomGenerator.generate()
    }.toList
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
    (1 to size).map { _ =>
      RandomGenerator.generate()
    }.toList
  }

  private def isUniqueSolution(
      solution: Solution,
      population: Population
  ): Boolean = {
    !population.solutions.exists(_.path == solution.path)
  }
}
