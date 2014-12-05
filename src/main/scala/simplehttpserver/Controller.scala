package simplehttpserver

import simplehttpserver.impl._

import scala.collection.immutable.HashMap


object Controller {
  type Action = HttpRequest => HttpResponse
  val builder = new HtmlBuilder()

  def default: Action = implicit req => {
    HttpResponse(Ok, HashMap(), "<p>welcome!</p>")
  }

  def echo: Action = implicit req => {
    HttpResponse(Ok, HashMap(), s"<p>${req.req}</p><p>${req.header.mkString}</p>")
  }

  def postPage: Action = implicit req => {
    HttpResponse(Ok, HashMap(), builder.buildHtml)
  }

  def blank: Action = implicit req => {
    HttpResponse(Ok, HashMap(), "")
  }
}
