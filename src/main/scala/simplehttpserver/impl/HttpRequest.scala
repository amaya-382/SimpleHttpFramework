package simplehttpserver.impl

import simplehttpserver.util.Security

import java.net.URLDecoder
import java.util.Date

case class HttpRequest(req: (Method, String, HttpVersion),
                       header: Map[String, String], body: Map[String, String] = Map()) {
  val cookie = getCookie
  private var _session = getSession
  def session = _session

  def getNewSession: HttpSession = {
    val SESSIONID = Security.hashBySHA384(header("Host") + new Date) //仮
    val newSession = HttpSession(SESSIONID, None, Map())
    _session = Some(newSession)
    newSession
  }

  private def getSession: Option[HttpSession] = {
    getCookie match {
      case c if c.size > 0 =>
        c.get("SESSIONID") map (HttpSession(_, None, Map()))
      case _ =>
        None
    }
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
}
