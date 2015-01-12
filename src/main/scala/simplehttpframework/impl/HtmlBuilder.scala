package simplehttpframework.impl

import scala.annotation.tailrec

//超簡易的なテンプレートエンジン
class HtmlBuilder {
  def buildHtml(template: String)(patterns: (String, String)*): String = {

    @tailrec
    def go(pat: Seq[(String, String)], acc: String): String = {
      if (pat.size == 0)
        acc.replaceAll( """(?<!@)@\w+?\(\((.+?)\)\)""", "$1")
      else
        go(pat.tail, acc.replaceAll("(?<!@)@" + pat.head._1,
          pat.head._2.replaceAll("@", "@@")))
    }

    go(patterns, template).replaceAll("@@", "@")
  }
}
