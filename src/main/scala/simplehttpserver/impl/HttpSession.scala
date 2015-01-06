package simplehttpserver.impl

import java.util.Date

case class HttpSession(sessionId: String, id: String,
                       expirse: Option[Date], data: Map[String, String]) {
  def isVaild: Boolean = {
    expirse map (new Date compareTo) match {
      case Some(x) if x > 0 => false
      case _ => true
    }
  }
}
