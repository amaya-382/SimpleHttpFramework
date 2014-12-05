package simplehttpserver.impl

import scala.collection.immutable.HashMap

case class HttpResponse(status: Status, header: HashMap[String, String], body: String)(implicit val req: HttpRequest)
