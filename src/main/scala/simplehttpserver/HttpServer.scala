package simplehttpserver

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{InetSocketAddress, ServerSocket, Socket, URLDecoder}

import simplehttpserver.impl._
import simplehttpserver.util.Util._

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


case class HttpServer(port: Int) {
  private val server = new ServerSocket()
  private val router = new Router()
  server.bind(new InetSocketAddress(port))
  println(s"start http server on $port")

  //regex
  private val rGET = """(?i)^GET\s([^\s]+)\sHTTP/(.+)$""".r
  private val rPOST = """(?i)^POST\s([^\s]+)\sHTTP/(.+)$""".r
  private val rPUT = """(?i)^PUT\s([^\s]+)\sHTTP/(.+)$""".r
  private val rDELETE = """(?i)^DELETE\s([^\s]+)\sHTTP/(.+)$""".r
  private val rHEAD = """(?i)^HEAD\s([^\s]+)\sHTTP/(.+)$""".r
  private val rOPTIONS = """(?i)^OPTIONS\s([^\s]+)\sHTTP/(.+)$""".r
  private val rTRACE = """(?i)^TRACE\s([^\s]+)\sHTTP/(.+)$""".r
  private val rCONNECT = """(?i)^CONNECT\s([^\s]+)\sHTTP/(.+)$""".r


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
            emitResponseFromFile(null)(InternalServerError, "500.html")
        }
        using(new PrintStream(socket.getOutputStream)) {
          out => response(out, res)
        }
      }
      }
    }

  private def response(ps: PrintStream, res: HttpResponse): Unit = {
    //response
    res.reqOpt match {
      case Some(req) =>
        ps.println(s"${req.req._3} ${res.status.code} ${res.status}")
      case None =>
        ps.println(s"HTTP/1.1 ${res.status.code} ${res.status}")
    }


    //header
    res.header.map(kv => kv._1 + ": " + kv._2).foreach(ps.println)

    ps.println()

    //body
    ps.write(res.body)

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

      request._1 getOrElse "" match {
        case rGET(cont, ver) =>
          println(s"get: $cont")
          Right(HttpRequest((GET, cont, HttpVersion(ver)), request._2))
        case rPOST(cont, ver) =>
          println(s"post: $cont")
          Right(HttpRequest((POST, cont, HttpVersion(ver)), request._2, request._3))
        case rPUT(cont, ver) =>
          println(s"put: $cont")
          Right(HttpRequest((PUT, cont, HttpVersion(ver)), request._2, request._3))
        case rDELETE(cont, ver) =>
          println(s"del: $cont")
          Right(HttpRequest((DELETE, cont, HttpVersion(ver)), request._2))
        case rHEAD(cont, ver) =>
          println(s"head: $cont")
          Right(HttpRequest((HEAD, cont, HttpVersion(ver)), request._2, request._3))
        case rOPTIONS(cont, ver) =>
          println(s"opt: $cont")
          Right(HttpRequest((OPTIONS, cont, HttpVersion(ver)), request._2, request._3))
        case rTRACE(cont, ver) =>
          println(s"trc: $cont")
          Right(HttpRequest((TRACE, cont, HttpVersion(ver)), request._2, request._3))
        case rCONNECT(cont, ver) =>
          println(s"cct: $cont")
          Right(HttpRequest((CONNECT, cont, HttpVersion(ver)), request._2, request._3))
        case _ =>
          println("oops!")
          Left(new Exception("request didn't match"))
      }
    }
    catch {
      case _: Throwable =>
        Left(new Exception("fail in parsing request"))
    }
  }

  private def readRequest(isr: InputStreamReader)
  : (Option[String], Map[String, String], Option[String]) = {
    val br = new BufferedReader(isr)
    val request = br.readLine()

    @tailrec
    def go(map: Map[String, String] = Map()): Map[String, String] = {
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
      if (request != null) Some(URLDecoder.decode(request, "utf-8")) else None,
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

