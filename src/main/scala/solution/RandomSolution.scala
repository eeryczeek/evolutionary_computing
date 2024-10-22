import scala.annotation.tailrec
import scala.util.Random

object RandomSolution {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int]
  ): (Solution, Set[Int]) = {
    if (currentSolution.path.size == problemInstance.expectedSolutionLen) {
      return (currentSolution, availableCities)
    }
    val nextCity = availableCities.toSeq(Random.nextInt(availableCities.size))
    val newCost = currentSolution.path match {
      case path: List[Int] if path == List.empty =>
        problemInstance.cityCosts(nextCity)
      case path: List[Int] =>
        currentSolution.cost +
          problemInstance.distances(path.last)(nextCity) +
          problemInstance.cityCosts(nextCity) +
          problemInstance.distances(nextCity)(path.head) -
          problemInstance.distances(path.last)(path.head)
    }
    (
      Solution(
        currentSolution.path :+ nextCity,
        newCost
      ),
      availableCities - nextCity
    )
  }
}
