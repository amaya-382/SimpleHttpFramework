package simplehttpserver.util

import java.security.MessageDigest

import simplehttpserver.util.Common.byteArray2HexString
import simplehttpserver.util.implicits.Implicit._

object Security {
  private val sha384 = MessageDigest.getInstance("SHA-384")

  def escape(str: String): Option[String] = {
    Option(str) flatMap escapeHTML flatMap dropCtrlChar
  }

  def escapeHTML(html: String): Option[String] = {
    if (html == null)
      None
    else {
      val sb = new StringBuilder

      html foreach {
        case '&' => sb.append("&amp;")
        case '<' => sb.append("&lt;")
        case '>' => sb.append("&gt;")
        case '"' => sb.append("&quot;")
        case '\'' => sb.append("&#39;")
        case s => sb.append(s)
      }

      Some(sb.toString())
    }
  }

  def dropCtrlChar(str: String): Option[String] = {
    if (str == null)
      None
    else
      Some(str.replaceAll( """\p{C}""", ""))
  }

  def hashBySHA384(seed: String): String = {
    sha384.update(seed)
    byteArray2HexString(sha384.digest())
  }
}
