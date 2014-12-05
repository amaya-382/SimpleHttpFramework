package simplehttpserver.impl

class HtmlBuilder {
  def buildHtml: String = {
    buildHeader + buildBody
  }

  private def buildHeader: String = {
    """<head><script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script></head>"""
  }

  private def buildBody: String = {
    s"""<html><body>$buildContent</body></html>"""
  }

  private def buildContent: String = {
    """<p>hello</p><button id="put">☆</button>
      |<form action="/postTest" method="post">
      |<p>お名前：<input type="text" name="namae" value="太郎" size="20" /></p>
      |<p>OS：
      |<input type="radio" name="OS" value="win" checked="checked" /> Windows
      |<input type="radio" name="OS" value="mac" /> Machintosh
      |<input type="radio" name="OS" value="unix" /> Unix
      |</p>
      |<p><input type="submit" name="submit" value="送信" /></p>
      |</form>
      |<form action="/postTest2" method="post">
      |<input type="submit" name="submit2" value="sousin" />
      |</form>
      |<script>$("#put").click(function(){$.ajax({"url":"/test","data":[1,2,3,4,5],"type":"POST","dataType":"json"})});</script>""".stripMargin
  }
}
