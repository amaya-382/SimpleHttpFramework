package simplehttpserver

import simplehttpserver.impl._
import simplehttpserver.util.Util._

import scala.collection.immutable.ListMap
import scala.util.matching.Regex

//TODO: errやblankといったページは外部に静的ファイルとして分離する
object Router {
  //ここでルーティングの設定を行う
  val routes = ListMap[(Method, Regex), HttpRequest => HttpResponse](
    (GET -> """^/postPage/?$""".r) -> Controller.postPage,
    (GET -> """^/echo/?$""".r) -> Controller.echo,
    (GET -> """^/blank/?$""".r) -> Controller.blank,
    (GET -> """^/$""".r) -> Controller.root,
    (GET -> """^/.+""".r) -> Controller.asset
  )
}
class Router {
  def routing(implicit req: HttpRequest): HttpResponse = {
    Router.routes.find(route => {
      req.req._1 == route._1._1 && route._1._2.findFirstIn(req.req._2).nonEmpty
    }) match {
      case Some(re) =>
        re._2(req)
      case None =>
        emitResponseFromFile(req)(NotFound, "404.html")
    }
  }
}

