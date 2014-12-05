package simplehttpserver.impl


case class HttpRequest(req: (Method, String, HttpVersion), header: Map[_ <: String, String], body: Option[String] = None) {

}
