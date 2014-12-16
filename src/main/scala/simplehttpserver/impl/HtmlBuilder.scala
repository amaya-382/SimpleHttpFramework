package simplehttpserver.impl

import scala.annotation.tailrec

//超簡易的なテンプレートエンジン
//一切エスケープを考慮していないので注意
class HtmlBuilder {
  def buildHtml(template: String)(patterns: (String, String)*): String = {

    @tailrec
    def go(pat: Seq[(String, String)], acc: String): String = {
      if (pat.size == 0)
        acc
      else
        go(pat.tail, acc.replaceAll("@" + pat.head._1, pat.head._2))
    }

    go(patterns, template)
  }
}
