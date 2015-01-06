package simplehttpserver.impl

import simplehttpserver.util.UseSession._

import java.net.URLDecoder
import java.util.Date

case class HttpRequest(req: (Method, String, HttpVersion),
                       header: Map[String, String],
                       body: Map[String, String] = Map()) {
  val cookie = getCookie
  private var _session = getSessionByRequest
  def session = _session

  def getNewSession: HttpSession = {
    val newSession =
      createNewSession(header("Host") + new Date, body.getOrElse("id", "")) //仮
    _session = Some(newSession)
    newSession
  }

  private def getCookie: Map[String, String] = {
    if (header.contains("Cookie"))
      header("Cookie").split(';').map(kv => {
        val kvs = kv.split('=').map(URLDecoder.decode(_, "utf-8").trim)
        kvs(0) -> (if (kvs.size > 1) kvs(1) else "")
      }).toMap
    else
      Map()
  }

  private def getSessionByRequest: Option[HttpSession] = {
    cookie.get("SESSIONID") flatMap getSessionBySessionId
  }
}
