import org.scalatest.funsuite.AnyFunSuite

class CSVReaderTest extends AnyFunSuite {

  test("should read correctly from CSV") {

    val problemInstance = CSVReader.readCSV("TSPTEST.csv")

    println(
      problemInstance.candidateEdges
        .map(_.mkString(" "))
        .mkString("\n")
    )
  }
}
