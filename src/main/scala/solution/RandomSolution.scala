import scala.annotation.tailrec
import scala.util.Random

object RandomSolution {
  def updateSolution(
      problemInstance: ProblemInstance,
      currentSolution: PartialSolution,
      availableCities: Set[Int]
  ): (PartialSolution, Set[Int]) = {
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
      PartialSolution(
        currentSolution.path :+ nextCity,
        newCost
      ),
      availableCities - nextCity
    )
  }
}
