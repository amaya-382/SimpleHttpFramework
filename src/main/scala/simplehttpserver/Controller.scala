package simplehttpserver

import java.io.{ByteArrayOutputStream, FileInputStream}

import simplehttpserver.impl._
import simplehttpserver.util.Util._
import simplehttpserver.util.Implicit._

import scala.sys.process.BasicIO


object Controller {
  type Action = HttpRequest => HttpResponse
  val builder = new HtmlBuilder()


  //TODO: Content-Typeの決定タイミングがバラバラ. Content-Typeとbodyはセットで生成されるべき

  def root: Action = req => {
    val contentType = "Content-Type" -> html.contentType
    HttpResponse(req)(
      Ok,
      header = Map(contentType),
      body = "<p>welcome!</p>")
  }

  def any: Action = req => {
    findAsset(req.req._2) match {
      case Some(file) =>
        println("asset found!")
        val out = new ByteArrayOutputStream()
        BasicIO.transferFully(new FileInputStream(file), out)

        HttpResponse(req)(
          Ok, body = out.toByteArray)
      case None =>
        emitResponseFromFile(req)(NotFound, "404.html")
    }
  }

  def echo: Action = req => {
    val contentType = "Content-Type" -> html.contentType

    HttpResponse(req)(
      Ok,
      header = Map(contentType),
      body = s"<p>${req.req}</p><p>${req.header.mkString}</p>")
  }

  def postPage: Action = req => {
    val contentType = "Content-Type" -> html.contentType

    HttpResponse(req)(
      Ok,
      header = Map(contentType),
      body = builder.buildHtml)
  }

  def blank: Action = req => {
    emitResponseFromFile(req)(Ok, "blank.html")
  }
}
