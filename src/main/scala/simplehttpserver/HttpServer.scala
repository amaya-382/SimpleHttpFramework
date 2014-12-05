package simplehttpserver

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{InetSocketAddress, ServerSocket, Socket, URLDecoder}
import java.util.{Date, Locale}

import simplehttpserver.impl._
import simplehttpserver.util.Util._

import scala.annotation.tailrec
import scala.collection.immutable.HashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


case class HttpServer(port: Int) {
  val server = new ServerSocket()
  val router = new Router()
  server.bind(new InetSocketAddress(port))
  println(s"start http server on $port")

  //regex
  val rGET = """(?i)^GET\s([^\s]+)\sHTTP/(.+)$""".r
  val rPOST = """(?i)^POST\s([^\s]+)\sHTTP/(.+)$""".r
  val rPUT = """(?i)^PUT\s([^\s]+)\sHTTP/(.+)$""".r
  val rDELETE = """(?i)^DELETE\s([^\s]+)\sHTTP/(.+)$""".r


  def start(): Unit = {
    while (true) {
      println("waiting connection")
      using(server.accept) { socket => {
        println("connected")
        Await.result(exe(socket), Duration.Inf)
      }
      }
    }
  }

  private def exe: Socket => Future[Unit] =
    socket => Future {
      using(new InputStreamReader(socket.getInputStream)) { in => {
        val res = parseRequest(in) match {
          case Right(r) =>
            router.routing(r)
          case Left(e) =>
            HttpResponse(InternalServerError, HashMap(), s"<p>Internal Server Error…</p><p>$e</p>")(null)
        }
        using(new PrintStream(socket.getOutputStream)) {
          out => response(out, res)
        }
      }
      }
    }

  private def response(ps: PrintStream, res: HttpResponse): Unit = {
    //response
    if (res.req != null)
      ps.println(s"${res.req.req._3.toString} ${res.status.code} ${res.status.toString}")
    else
      ps.println(s"HTTP/1.1 ${res.status.code} ${res.status.toString}")

    //header
    ps.println("Date: " + "%ta, %<td %<tb %<tY %<tT %<tz" formatLocal(Locale.ENGLISH, new Date))
    ps.println("Server: Simple Http Server")
    ps.println("Content-type: text/html") /////////////////////
    res.header.map(kv => kv._1 + ": " + kv._2).foreach(ps.println)

    ps.println()

    //body
    ps.println(res.body)

    ps.flush()
  }

  private def parseRequest(isr: InputStreamReader): Either[Throwable, HttpRequest] = {
    try {
      val request =
        try {
          readRequest(isr)
        } catch {
          case ex: Throwable =>
            println(ex)
            throw new Exception("fail in reading request")
        }

      request._1 match {
        case Some(rGET(cont, ver)) =>
          println(s"get: $cont")
          Right(HttpRequest((GET, cont, HttpVersion(ver)), request._2))
        case Some(rPOST(cont, ver)) =>
          println(s"post: $cont")
          Right(HttpRequest((POST, cont, HttpVersion(ver)), request._2, request._3))
        case Some(rPUT(cont, ver)) =>
          println(s"put: $cont")
          Right(HttpRequest((PUT, cont, HttpVersion(ver)), request._2, request._3))
        case Some(rDELETE(cont, ver)) =>
          println(s"del: $cont")
          Right(HttpRequest((DELETE, cont, HttpVersion(ver)), request._2))
        case _ =>
          println("opp")
          Left(new Exception("request didn't match"))
      }
    } catch {
      case _: Throwable =>
        Left(new Exception("fail in parsing request"))
    }
  }

  private def readRequest(isr: InputStreamReader): (Option[String], HashMap[String, String], Option[String]) = {
    val br = new BufferedReader(isr)
    val request = br.readLine()

    @tailrec
    def go(map: HashMap[String, String] = HashMap()): HashMap[String, String] = {
      val line = br.readLine()

      if (line == null || line == "") map
      else {
        val kv = line.splitAt(line.indexOf(":"))
        println(s"kv: $kv")
        if (kv._1.nonEmpty) {
          go(map + (kv._1.trim -> (if (kv._2.nonEmpty) kv._2.tail.trim else "")))
        }
        else go(map)
      }
    }

    val header = go()
    (
      //request
      if (request != null) Some(request) else None,
      //header
      header,
      //body if exists
      if (header.contains("Content-Length")) {
        Some({
          val x = new Array[Char](header("Content-Length").toInt)
          br.read(x)
          URLDecoder.decode(x.mkString, "utf-8") //TODO: impl encoding
        })
      } else None)
  }
}

