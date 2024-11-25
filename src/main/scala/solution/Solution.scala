import scala.util.Random
import scala.util.control.TailCalls.TailRec
import scala.annotation.tailrec

case class Solution(path: Array[Int], cost: Int)

object SolutionFactory {
  def getRandomSolution(): Solution = {
    generate(
      Solution(Array.empty, 0),
      ProblemInstanceHolder.problemInstance.cities,
      RandomSolution.updateSolution
    )
  }

  def getGreedyTailSolution(initialCity: Int): Solution = {
    generate(
      Solution(
        Array(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyTailSolution.updateSolution
    )
  }

  def getGreedyAnyPositionSolution(initialCity: Int): Solution = {
    generate(
      Solution(
        Array(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyAtAnyPositionSolution.updateSolution
    )
  }

  def getGreedyCycleSolution(initialCity: Int): Solution = {
    generate(
      Solution(
        Array(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyCycleSolution.updateSolution
    )
  }

  def getGreedyCycleRegretSolution(initialCity: Int): Solution = {
    generate(
      Solution(
        Array(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyCycleRegretSolution.updateSolution
    )
  }

  def getGreedyCycleWeightedRegretSolution(initialCity: Int): Solution = {
    generate(
      Solution(
        Array(initialCity),
        ProblemInstanceHolder.problemInstance.cityCosts(initialCity)
      ),
      ProblemInstanceHolder.problemInstance.cities - initialCity,
      GreedyCycleWeightedRegretSolution.updateSolution _
    )
  }

  def getLocalSearchWithEdgesSwapsGreedyRandomStart(
      initialCity: Int
  ): Solution = {
    val randomSolution = getRandomSolution()
    generate(
      randomSolution,
      ProblemInstanceHolder.problemInstance.cities -- randomSolution.path,
      LocalSearchWithEdgesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithEdgesSwapsGreedyHeuristicStart(
      initialCity: Int
  ): Solution = {
    val initialSolution =
      getGreedyAnyPositionSolution(initialCity)
    generate(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithEdgesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithEdgesSwapsSteepestRandomStart(
      optionalInitialSolution: Option[Solution] = None
  ): Solution = {
    val initialSolution =
      optionalInitialSolution.getOrElse(getRandomSolution())
    generate(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithEdgesSwapsSteepest.updateSolution
    )
  }

  def getLocalSearchWithEdgesSwapsSteepestHeuristicStart(
      initialCity: Int
  ): Solution = {
    val initialSolution =
      getGreedyAnyPositionSolution(initialCity)
    generate(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithEdgesSwapsSteepest.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsGreedyRandomStart(
      initialCity: Int
  ): Solution = {
    val randomSolution = getRandomSolution()
    generate(
      randomSolution,
      ProblemInstanceHolder.problemInstance.cities -- randomSolution.path,
      LocalSearchWithNodesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsGreedyHeuristicStart(
      initialCity: Int
  ): Solution = {
    val initialSolution =
      getGreedyAnyPositionSolution(initialCity)
    generate(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithNodesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsSteepestRandomStart(
      initialCity: Int
  ): Solution = {
    val randomSolution = getRandomSolution()
    generate(
      randomSolution,
      ProblemInstanceHolder.problemInstance.cities -- randomSolution.path,
      LocalSearchWithNodesSwapsSteepest.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsSteepestHeuristicStart(
      initialCity: Int
  ): Solution = {
    val initialSolution =
      getGreedyAnyPositionSolution(initialCity)
    generate(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithNodesSwapsSteepest.updateSolution
    )
  }

  def getLocalsearchWithCandidateMovesGreedyRandomStart(
      initialCity: Int
  ): Solution = {
    val initialSolution = getRandomSolution()
    generate(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithCandidateMovesGreedy.updateSolution
    )
  }

  def getLocalsearchWithCandidateMovesSteepestRandomStart(
      initialCity: Int
  ): Solution = {
    val initialSolution = getRandomSolution()
    generate(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path,
      LocalSearchWithCandidateMovesSteepest.updateSolution
    )
  }

  def getLocalSearchWithListOfImprovingMoves(
      optionalInitialSolution: Option[Solution] = None
  ): Solution = {
    val solution = optionalInitialSolution.getOrElse(getRandomSolution())
    val localSearchInstance = ListOfImprovingMovesSolution(
      solution,
      ProblemInstanceHolder.problemInstance.cities -- solution.path
    )
    generate(
      solution,
      ProblemInstanceHolder.problemInstance.cities -- solution.path,
      localSearchInstance.updateSolution
    )
  }

  def getIteratedLocalSearch(initialCity: Int): Solution = {
    val initialSolution = getRandomSolution()
    val updatedSolution = IteratedLSSolution.updateSolution(
      initialSolution,
      ProblemInstanceHolder.problemInstance.cities -- initialSolution.path
    )
    updatedSolution
  }

  // def getSteepest(
  //     problemInstance: ProblemInstance,
  //     initialSolution: Solution
  // ): Solution = {
  //   val initialSol = initialSolution
  //   var solSteepest = initialSolution
  //   var prevList = initialSolution
  //   var solList = initialSolution
  //   var shouldContinue = true
  //   val list = ListOfImprovingMovesSolution(
  //     problemInstance,
  //     initialSolution,
  //     problemInstance.cities -- initialSolution.path
  //   )

  //   val initialMovesList = list.improvingMoves.toSeq

  //   while (shouldContinue) {
  //     solSteepest = LocalSearchWithEdgesSwapsSteepest
  //       .updateSolution(
  //         problemInstance,
  //         solList,
  //         problemInstance.cities -- solList.path
  //       )
  //       ._1

  //     prevList = solList
  //     solList = list
  //       .updateSolution(
  //         problemInstance,
  //         solList,
  //         problemInstance.cities -- solList.path
  //       )
  //       ._1

  //     shouldContinue = solSteepest.cost == solList.cost && solList != prevList
  //     if (!shouldContinue) {
  //       println("Solution path: " + solList.path.mkString(", "))
  //       val bestSteepestMove = MoveHistory.getHistorySteepest.last._1
  //       println("Edges: " + list.getConsecutivePairs(prevList).mkString(", "))
  //       println
  //       println(
  //         "Triplets: " + list.getConsecutiveTriplets(prevList).mkString(", ")
  //       )

  //       println()
  //       println("History List:")
  //       MoveHistory.getHistoryList.foreach(x =>
  //         println(s"${x._1} -> ${x._2.path.mkString(",")}")
  //       )
  //     }
  //   }

  //   solSteepest
  // }

  // def getMSLS(
  //     problemInstance: ProblemInstance,
  //     initialCity: Int
  // ): Solution = {
  //   val startTime = System.currentTimeMillis()
  //   val sol = MSLS.run(problemInstance)
  //   val endTime = System.currentTimeMillis()
  //   println(s"Time: ${endTime - startTime} ms")
  //   sol
  // }

  @tailrec
  def generate(
      currentSolution: Solution,
      remainingCities: Set[Int],
      updateSolution: (Solution, Set[Int]) => (Solution, Set[Int])
  ): Solution = {
    val (updatedSolution, updatedRemainingCities) =
      updateSolution(currentSolution, remainingCities)

    if (updatedSolution == currentSolution) {
      currentSolution
    } else {
      generate(updatedSolution, updatedRemainingCities, updateSolution)
    }
  }
}
