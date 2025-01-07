import scala.util.Random

object DestructionUtils extends CostManager {
  def destroy(solution1: Solution, solution2: Solution): Seq[Seq[Int]] = {
    val path1 = solution1.path
    val path2 = solution2.path

    var idx = 0
    var sameNodeFound = false
    while (idx < 100 && !sameNodeFound) {
      if (path1(idx) == path2(idx)) {
        sameNodeFound = true
      }
      idx += 1
    }

    // i need to return n parts of the path that are common in both solutions
    // for example, f([1, 2, 3, 4, 5], [1, 2, 4, 5, 6]) = [[1, 2], [4, 5]]
    if (sameNodeFound) {
      val startIdx = idx
      var parts = Seq[Seq[Int]]()
      var currentPart = Seq[Int]()
      while (idx + 1 != startIdx) {
        if (path1(idx) == path2(idx)) {
          currentPart = currentPart :+ path1(idx)
        } else {
          if (currentPart.nonEmpty) {
            parts = parts :+ currentPart
            currentPart = Seq[Int]()
          }
        }
        idx = (idx + 1) % 100
      }
      parts
    } else {
      Seq[Seq[Int]]()
    }
  }

  def mergePartsRandomly(
      parts: Seq[Seq[Int]],
      availableCities: Set[Int]
  ): Seq[Int] = {
    var randomOrderedAvailableCities = Random.shuffle(availableCities.toSeq)
    var partsToEdit = parts
    var lenght = parts.map(_.length).sum
    while (lenght < 100) {
      val randomCity = randomOrderedAvailableCities.head
      randomOrderedAvailableCities = randomOrderedAvailableCities.tail
      val randomPartIdx = Random.nextInt(partsToEdit.length)
      val randomPart = partsToEdit(randomPartIdx)
      partsToEdit = partsToEdit.updated(
        randomPartIdx,
        randomPart :+ randomCity
      )
      lenght += 1
    }

    partsToEdit.flatten
  }

  def mergePartsHeuristically(
      parts: Seq[Seq[Int]],
      availableCities: Set[Int]
  ): Seq[Int] = {
    var partsToEdit = parts :+ parts.head
    var currentlyAvailableCities = availableCities
    var lenght = parts.map(_.length).sum
    while (lenght < 100) {
      val (bestCityToInsert, idxOfPartToAppendAfter, _) =
        currentlyAvailableCities
          .flatMap { city =>
            for ((twoParts, idx) <- partsToEdit.sliding(2).zipWithIndex) yield {
              val part1 = twoParts.head
              val part2 = twoParts.last
              (
                city,
                idx,
                getDeltaCost(InsertBetween(Pair(part1.last, part2.head), city))
              )
            }
          }
          .minBy(_._3)
      partsToEdit = partsToEdit.updated(
        idxOfPartToAppendAfter,
        partsToEdit(idxOfPartToAppendAfter) :+ bestCityToInsert
      )
      lenght += 1
    }

    partsToEdit.take(partsToEdit.size - 1).flatten
  }
}
