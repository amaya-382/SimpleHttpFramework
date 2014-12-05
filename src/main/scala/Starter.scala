import simplehttpserver.HttpServer

object Starter {
  def main(args: Array[String]) {
    val server = HttpServer(8080)
    server.start()
  }
}
