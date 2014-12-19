package simplehttpserver.impl


case class HttpRequest(req: (Method, String, HttpVersion),
                       header: Map[String, String], body: Map[String, String] = Map())
