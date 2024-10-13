import scala.io.Source

object TXTWriter {
  def writeTXT(filePath: String, solution: Solution): Unit = {
    val writer = new java.io.PrintWriter(filePath)
    solution match {
      case FullSolution(path, cost) =>
        writer.write(s"Cost: ${cost}\n")
        path.foreach(city => writer.write(s"${city.id} ${city.x} ${city.y}\n"))
      case FaultySolution(path, cost, reason) =>
        writer.write(s"Cost: ${cost}\n")
        writer.write(s"Reason: ${reason}\n")
        path.foreach(city => writer.write(s"${city.id} ${city.x} ${city.y}\n"))
    }
    writer.close()
  }
}
