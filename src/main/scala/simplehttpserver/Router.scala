package simplehttpserver

import simplehttpserver.impl._
import simplehttpserver.util.EasyEmit

import scala.reflect.runtime._
import scala.util.matching.Regex

import org.json4s._
import org.json4s.native.JsonMethods

class Router extends EasyEmit {
  implicit private val formats = DefaultFormats
  private val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
  private val getObj = getObjectByName[Method]("simplehttpserver.impl") _

  //TODO: routingTable取得元の調整
    private val json = getStringFromFile("./private/routingTable.json") match {
    case Some(data) => JsonMethods.parse(data)
    case None => throw new Exception("route file not found")
  }

  private val routes: List[(Method, Regex, (java.lang.reflect.Method, Any))] =
    for {
      JArray(arr) <- json
      JObject(table) <- arr
      JField("method", JString(method)) <- table
      JField("pattern", JString(pattern)) <- table
      JField("controller", JString(controller)) <- table
    } yield (getObj(method), pattern.r, getMethodByName(controller))

  def routing(implicit req: HttpRequest): HttpResponse = {
    routes
      .find(route =>
      req.req._1 == route._1 && route._2.findFirstIn(req.req._2).nonEmpty)
    match {
      case Some(re) =>
        re._3._1.invoke(re._3._2).asInstanceOf[HttpRequest => HttpResponse](req)
      case None =>
        emitError(req)(NotFound)
    }
  }

  private def getObjectByName[T](nameSpace: String)(objName: String): T = {
    val module = runtimeMirror.staticModule(nameSpace + "." + objName)
    val obj = runtimeMirror.reflectModule(module)
    obj.instance.asInstanceOf[T]
  }

  private def getMethodByName(methodFullName: String): (java.lang.reflect.Method, Any) = {
    val regex = """(.+)\.([^\.]+)""".r
    val regex(path, methodName) = methodFullName

    val module = runtimeMirror.staticModule(path)
    val obj = runtimeMirror.reflectModule(module)
    val clazz = Class.forName(path)
    val method = clazz.getMethod(methodName)
    (method, obj.instance)
  }
}

