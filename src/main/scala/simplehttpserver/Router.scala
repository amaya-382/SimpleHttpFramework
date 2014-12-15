package simplehttpserver

import simplehttpserver.impl._
import simplehttpserver.util.Util._

import scala.reflect.runtime._
import scala.util.matching.Regex

import org.json4s._
import org.json4s.native.JsonMethods

class Router {
  implicit private val formats = DefaultFormats
  private val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
  private val getObj = getObjectByName[Method]("simplehttpserver.impl") _

  //TODO: data取得元の調整
  private val json = getStringFromResources("data.json") match {
    case Some(data) => JsonMethods.parse(data)
    case None => throw new Exception("route file not found")
  }

  private val routes: List[(Method, Regex, (java.lang.reflect.Method, Any))] =
    for {
      JArray(arr) <- json
      JObject(table) <- arr
      JField("method", JString(method)) <- table
      JField("regex", JString(regex)) <- table
      JField("controller", JString(controller)) <- table
    } yield (getObj(method), regex.r, getMethodByName(controller))

  def routing(implicit req: HttpRequest): HttpResponse = {
    routes
      .find(route =>
      req.req._1 == route._1 && route._2.findFirstIn(req.req._2).nonEmpty)
    match {
      case Some(re) =>
        re._3._1.invoke(re._3._2).asInstanceOf[HttpRequest => HttpResponse](req)
      case None =>
        emitResponseFromFile(req)(NotFound, "404.html")
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

