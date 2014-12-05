package simplehttpserver.impl

import java.util.{Date, Locale}

import simplehttpserver.util.Util._


class HttpResponse(val reqOpt: Option[HttpRequest])
                  (val status: Status, val header: Map[String, String], val body: Array[Byte])
object HttpResponse {
  def apply(req: HttpRequest = null)
           (status: Status, header: Map[String, String] = Map(),
            body: Array[Byte]): HttpResponse = {
    val reqOpt = Option(req)

    val defaultHeader = {
      val dateHeader =
        "Date" -> ("%ta, %<td %<tb %<tY %<tT %<tz" formatLocal(Locale.ENGLISH, new Date))
      val serverHeader =
        "Server" -> "Simple Http Server"
      val contentTypeHeader = {
        val ext = reqOpt.flatMap(_.req._2.split('.').lastOption)
        "Content-Type" -> getContentType(ext getOrElse "").contentType
      }

      Map(dateHeader, serverHeader, contentTypeHeader)
    }

    new HttpResponse(reqOpt)(status, defaultHeader ++ header, body)
  }
}
