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
