import scala.util.Random
import scala.util.control.TailCalls.TailRec
import scala.annotation.tailrec

case class Solution(path: Array[Int], cost: Int)

object SolutionFactory {
  def getRandomSolution(
      problemInstance: ProblemInstance,
      initialCity: Int = 0
  ): Solution = {
    generate(
      problemInstance,
      Solution(Array.empty, 0),
      problemInstance.cities,
      RandomSolution.updateSolution
    )
  }

  def getGreedyTailSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    generate(
      problemInstance,
      Solution(
        Array(initialCity),
        problemInstance.cityCosts(initialCity)
      ),
      problemInstance.cities - initialCity,
      GreedyTailSolution.updateSolution
    )
  }

  def getGreedyAnyPositionSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    generate(
      problemInstance,
      Solution(
        Array(initialCity),
        problemInstance.cityCosts(initialCity)
      ),
      problemInstance.cities - initialCity,
      GreedyAtAnyPositionSolution.updateSolution
    )
  }

  def getGreedyCycleSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    generate(
      problemInstance,
      Solution(
        Array(initialCity),
        problemInstance.cityCosts(initialCity)
      ),
      problemInstance.cities - initialCity,
      GreedyCycleSolution.updateSolution
    )
  }

  def getGreedyCycleRegretSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    generate(
      problemInstance,
      Solution(
        Array(initialCity),
        problemInstance.cityCosts(initialCity)
      ),
      problemInstance.cities - initialCity,
      GreedyCycleRegretSolution.updateSolution
    )
  }

  def getGreedyCycleWeightedRegretSolution(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    generate(
      problemInstance,
      Solution(
        Array(initialCity),
        problemInstance.cityCosts(initialCity)
      ),
      problemInstance.cities - initialCity,
      GreedyCycleWeightedRegretSolution.updateSolution
    )
  }

  def getLocalSearchWithEdgesSwapsGreedyRandomStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val randomSolution = getRandomSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      randomSolution,
      problemInstance.cities -- randomSolution.path,
      LocalSearchWithEdgesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithEdgesSwapsGreedyHeuristicStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val initialSolution =
      getGreedyAnyPositionSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path,
      LocalSearchWithEdgesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithEdgesSwapsSteepestRandomStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val randomSolution = getRandomSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      randomSolution,
      problemInstance.cities -- randomSolution.path,
      LocalSearchWithEdgesSwapsSteepest.updateSolution
    )
  }

  def getLocalSearchWithEdgesSwapsSteepestHeuristicStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val initialSolution =
      getGreedyAnyPositionSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path,
      LocalSearchWithEdgesSwapsSteepest.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsGreedyRandomStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val randomSolution = getRandomSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      randomSolution,
      problemInstance.cities -- randomSolution.path,
      LocalSearchWithNodesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsGreedyHeuristicStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val initialSolution =
      getGreedyAnyPositionSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path,
      LocalSearchWithNodesSwapsGreedy.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsSteepestRandomStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val randomSolution = getRandomSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      randomSolution,
      problemInstance.cities -- randomSolution.path,
      LocalSearchWithNodesSwapsSteepest.updateSolution
    )
  }

  def getLocalSearchWithNodesSwapsSteepestHeuristicStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val initialSolution =
      getGreedyAnyPositionSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path,
      LocalSearchWithNodesSwapsSteepest.updateSolution
    )
  }

  def getLocalsearchWithCandidateMovesGreedyRandomStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val initialSolution = getRandomSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path,
      LocalSearchWithCandidateMovesGreedy.updateSolution
    )
  }

  def getLocalsearchWithCandidateMovesSteepestRandomStart(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val initialSolution = getRandomSolution(problemInstance, initialCity)
    generate(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path,
      LocalSearchWithCandidateMovesSteepest.updateSolution
    )
  }

  def getLocalSearchWithListOfImprovingMoves(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val initialSolution = getRandomSolution(problemInstance, initialCity)
    val localSearchInstance = ListOfImprovingMovesSolution(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path
    )
    generate(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path,
      localSearchInstance.updateSolution
    )
  }

  def getSteepest(
      problemInstance: ProblemInstance,
      initialSolution: Solution
  ): Solution = {
    val initialSol = initialSolution
    var solSteepest = initialSolution
    var prevList = initialSolution
    var solList = initialSolution
    var shouldContinue = true
    val list = ListOfImprovingMovesSolution(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path
    )

    val initialMovesList = list.improvingMoves.toSeq

    while (shouldContinue) {
      solSteepest = LocalSearchWithEdgesSwapsSteepest
        .updateSolution(
          problemInstance,
          solList,
          problemInstance.cities -- solList.path
        )
        ._1

      prevList = solList
      solList = list
        .updateSolution(
          problemInstance,
          solList,
          problemInstance.cities -- solList.path
        )
        ._1

      shouldContinue = solSteepest.cost == solList.cost && solList != prevList
      if (!shouldContinue) {
        println("Solution path: " + solList.path.mkString(", "))
        val bestSteepestMove = MoveHistory.getHistorySteepest.last._1
        println("Edges: " + list.getConsecutivePairs(prevList).mkString(", "))
        println
        println(
          "Triplets: " + list.getConsecutiveTriplets(prevList).mkString(", ")
        )

        println()
        println("History List:")
        MoveHistory.getHistoryList.foreach(x =>
          println(s"${x._1} -> ${x._2.path.mkString(",")}")
        )
      }
    }

    solSteepest
  }

  def getLocalSearchWithListOfImprovingMoves(
      problemInstance: ProblemInstance,
      initialSolution: Solution
  ): Solution = {
    val localSearchInstance = ListOfImprovingMovesSolution(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path
    )
    generate(
      problemInstance,
      initialSolution,
      problemInstance.cities -- initialSolution.path,
      localSearchInstance.updateSolution
    )
  }

  def getMSLS(
      problemInstance: ProblemInstance,
      initialCity: Int
  ): Solution = {
    val startTime = System.currentTimeMillis()
    val sol = MSLS.run(problemInstance)
    val endTime = System.currentTimeMillis()
    println(s"Time: ${endTime - startTime} ms")
    sol
  }

  @tailrec
  def generate(
      problemInstance: ProblemInstance,
      currentSolution: Solution,
      availableCities: Set[Int],
      updateSolution: (
          ProblemInstance,
          Solution,
          Set[Int]
      ) => (Solution, Set[Int])
  ): Solution = {
    val (newSolution, newAvailableCities) = updateSolution(
      problemInstance,
      currentSolution,
      availableCities
    )

    if (newSolution == currentSolution) {
      Solution(currentSolution.path, currentSolution.cost)
    } else {
      generate(
        problemInstance,
        newSolution,
        newAvailableCities,
        updateSolution
      )
    }
  }

}
