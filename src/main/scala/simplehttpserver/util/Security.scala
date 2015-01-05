package simplehttpserver.util

object Security {
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
}
