package simplehttpserver.impl

import scala.collection.immutable.HashMap

case class HttpRequest(req: (Method, String, HttpVersion), header: HashMap[_ <: String, String], body: Option[String] = None) {

}
