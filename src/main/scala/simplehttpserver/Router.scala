package simplehttpserver

import simplehttpserver.impl._

import scala.collection.immutable.{HashMap, ListMap}
import scala.util.matching.Regex


object Router {
  //ここでルーティングの設定を行う
  val routes = ListMap[(Method, Regex), HttpRequest => HttpResponse](
    (GET -> """^/postPage""".r) -> Controller.postPage,
    (GET -> """^/echo""".r) -> Controller.echo,
    (POST -> """^/blank""".r) -> Controller.blank,
    (GET -> """^/.*""".r) -> Controller.default
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
        HttpResponse(NotFound, HashMap(), "<h2>404 Not Found</h2><hr>") //TODO: impl err
    }
  }
}

