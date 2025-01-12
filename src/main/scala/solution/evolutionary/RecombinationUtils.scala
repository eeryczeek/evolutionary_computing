import scala.util.Random

object RecombinationUtils extends CostManager {
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

  def destroy2(solution1: Solution, solution2: Solution): Seq[Seq[Int]] = {
    val path1 = solution1.path
    val path2 = solution2.path
    val commonCities = path1.filter(path2.contains)

    // find common edges
    val edgesPath1 = path1.sliding(2).map(_.toSet).toSet
    val edgesPath2 = path2.sliding(2).map(_.toSet).toSet

    val commonEdges = edgesPath1.intersect(edgesPath2)

    // println(s"Number of common edges: ${commonEdges.size}")

    val edgesAsPairs =
      commonEdges.map(_.toList).map { case List(a, b) => Pair(a, b) }.toList

    def findMatchingPart(
        pairToMatch: Pair,
        parts: Seq[(Set[Int], Seq[Int])]
    ): Option[(Int, Set[Int], Seq[Int])] = {
      parts.zipWithIndex
        .find { case ((partCities, part), _) =>
          partCities.contains(pairToMatch.city1) ||
          partCities.contains(pairToMatch.city2)
        }
        .map { case ((partCities, part), idx) =>
          (idx, partCities, part)
        }
    }

    def updatePart(
        pair: Pair,
        part: Seq[Int]
    ): Seq[Int] = {
      if (part.head == pair.city1) {
        pair.city2 +: part
      } else if (part.head == pair.city2) {
        pair.city1 +: part
      } else if (part.last == pair.city1) {
        part :+ pair.city2
      } else if (part.last == pair.city2) {
        part :+ pair.city1
      } else {
        part
      }
    }

    // merge together consecutive pairs
    val mergedPairs = edgesAsPairs
      .foldLeft(Seq[(Set[Int], Seq[Int])]()) { (acc, pair) =>
        val matchedPart = findMatchingPart(pair, acc)
        matchedPart match {
          case Some((idx, partCities, part)) =>
            val newPart = updatePart(pair, part)
            val newPartCities = partCities + pair.city1 + pair.city2
            acc.updated(idx, (newPartCities, newPart))
          case None =>
            acc :+ (Set(pair.city1, pair.city2), Seq(pair.city1, pair.city2))
        }
      }
      .map(_._2)

    // glue together ends of parts which has the same city
    val gluedParts = mergedPairs
      .foldLeft(Seq[Seq[Int]]()) { (acc, part) =>
        // Find all parts in the accumulator that connect with the current part
        val (matchingParts, nonMatchingParts) = acc.partition { accPart =>
          accPart.head == part.head || accPart.last == part.last ||
          accPart.head == part.last || accPart.last == part.head
        }

        if (matchingParts.isEmpty) {
          // If no matching part is found, just add the part to the accumulator
          nonMatchingParts :+ part
        } else {
          // Merge all matching parts with the current part
          val mergedPart = matchingParts.foldLeft(part) { (merged, accPart) =>
            if (accPart.head == merged.head) {
              merged.reverse ++ accPart.drop(1)
            } else if (accPart.head == merged.last) {
              merged ++ accPart.drop(1)
            } else if (accPart.last == merged.head) {
              accPart ++ merged.drop(1)
            } else if (accPart.last == merged.last) {
              accPart ++ merged.reverse.drop(1)
            } else {
              merged // This should not happen
            }
          }

          // Add the merged part back to the accumulator
          nonMatchingParts :+ mergedPart
        }
      }

    if (gluedParts.isEmpty && commonCities.isEmpty) {
      Seq(path1.take(10), path1.drop(10).take(10))
    } else if (gluedParts.isEmpty) {
      Seq(commonCities)
    } else {
      gluedParts
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
      val randomPartIdx =
        if (partsToEdit.length > 1) Random.nextInt(partsToEdit.length - 1)
        else 0
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
      currentlyAvailableCities = currentlyAvailableCities - bestCityToInsert
    }

    partsToEdit.take(partsToEdit.size - 1).flatten
  }
}
