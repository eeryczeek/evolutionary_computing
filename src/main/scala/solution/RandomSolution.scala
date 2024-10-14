import scala.annotation.tailrec
import scala.util.Random

object RandomSolution {
  @tailrec
  def generate(
      problemInstance: ProblemInstance,
      currentSolution: PartialSolution,
      availableCities: Set[Int]
  ): FullSolution = {
    if (currentSolution.path.size == problemInstance.expectedSolutionLen) {
      FullSolution(
        currentSolution.path,
        currentSolution.cost + problemInstance.distances(
          currentSolution.path.last
        )(currentSolution.path.head)
      )
    } else {
      val (newSolution, newAvailableCities) =
        updateSolution(problemInstance, currentSolution, availableCities)
      generate(
        problemInstance,
        newSolution,
        newAvailableCities
      )
    }
  }

  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: PartialSolution,
      availableCities: Set[Int]
  ): (PartialSolution, Set[Int]) = {
    val nextCity = availableCities.toSeq(Random.nextInt(availableCities.size))
    val lastCityCost =
      if (currentSolution.path.isEmpty) 0
      else problemInstance.distances(currentSolution.path.last)(nextCity)
    (
      PartialSolution(
        currentSolution.path :+ nextCity,
        currentSolution.cost + lastCityCost
      ),
      availableCities - nextCity
    )
  }
}
