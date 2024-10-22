import scala.io.Source

object TXTWriter {
  def writeTXT(filePath: String, solution: Solution): Unit = {
    val writer = new java.io.PrintWriter(filePath)
    writer.write(s"Cost: ${solution.cost}\n")
    writer.write(s"Path: ${solution.path.mkString(",")}\n")
    writer.close()
  }
}
