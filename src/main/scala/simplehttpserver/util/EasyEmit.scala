package simplehttpserver.util

import simplehttpserver.impl._
import simplehttpserver.util.implicits.Implicit._
import simplehttpserver.util.Common._

trait EasyEmit extends UseResources {
  //publicにあればそれを, なければResourcesからファイルを探し, その文字列を返します
  def emitResponseFromFile(req: HttpRequest)
                          (status: Status, name: String): HttpResponse = {
    val bodyOpt =
      getStringFromFile("./public/" + name) match {
        case None => getStringFromResources(name)
        case opt => opt
      }

    bodyOpt match {
      case Some(body) =>
        val contentType = {
          val ext = name.split('.').lastOption getOrElse ""
          "Content-Type" -> getContentType(ext).contentType
        }
        HttpResponse(req)(status, Map(contentType), body)
      case None =>
        HttpResponse(req)(status, body = "")
    }
  }

  def emitError(req: HttpRequest)(status: Status): HttpResponse = {
    val bodyOpt =
      getStringFromFile("./public/errBase.html") match {
        case None => getStringFromResources("errBase.html")
        case opt => opt
      }

    bodyOpt match {
      case Some(body) =>
        val builder = new HtmlBuilder().buildHtml(body) _
        val contentType = "Content-Type" -> html.contentType
        HttpResponse(req)(
          status,
          Map(contentType),
          builder(Seq("msg" -> s"${status.code.toString} ${status.toString}…")))
      case None =>
        HttpResponse(req)(status, body = "")
    }
  }
}
