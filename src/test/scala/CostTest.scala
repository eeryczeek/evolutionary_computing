import org.scalatest.funsuite.AnyFunSuite
class CostTest extends AnyFunSuite {
  test("distance between same point should be 0") {}
}

class readCSVTest extends AnyFunSuite {
  test("readCSV should return InitialData") {
    val filePath = "TSPA.csv"
    val result = CSVReader.readCSV(filePath)
    assert(result.isInstanceOf[ProblemInstance])
  }
}

class slidingTest extends AnyFunSuite {
  test("sliding should return List of List with wrap-around") {
    val list = List(1, 2, 3, 4, 5)
    val extendedList =
      list ++ list.take(2) // Concatenate the list with its first two elements
    val result =
      extendedList.sliding(3).toList.take(5) // Take only the first 5 elements
    println(result)
    assert(
      result == List(
        List(1, 2, 3),
        List(2, 3, 4),
        List(3, 4, 5),
        List(4, 5, 1),
        List(5, 1, 2)
      )
    )
  }
}
