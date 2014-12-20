package simplehttpserver

import java.io._
import java.net.{InetSocketAddress, ServerSocket, Socket, URLDecoder}
import java.util.zip.{GZIPOutputStream, Deflater}

import simplehttpserver.impl._
import simplehttpserver.util.Util._

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.control.Exception._


case class HttpServer(port: Int) {
  private val server = new ServerSocket()
  private val router = new Router()
  server.bind(new InetSocketAddress(port))
  println(s"start http server on $port")

  //regex
  private val rReq = """(?i)^(\w{3,7})\s(([^\s\.]|[^\s\.]\.)+)\sHTTP/(.+)$""".r

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
          case Left(s) =>
            //TODO: impl log
            emitError(null)(s)
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

    //header, body
    val ecdOpt = res.reqOpt.flatMap(_.header.get("Accept-Encoding"))
    ecdOpt match {
      //gzip
      case Some(e) if res.header.getOrElse("Content-Type", "").startsWith("text")
        && e.indexOf("gzip") != -1 =>
        writeWithGZIP(ps, res)

      //deflate
      case Some(e) if res.header.getOrElse("Content-Type", "").startsWith("text")
        && e.indexOf("deflate") != -1 =>
        writeWithDEFLATE(ps, res)

      //non-compression
      case _ =>
        writeWithoutCompression(ps, res)
    }
  }

  private def writeWithoutCompression(ps: PrintStream, res: HttpResponse): Unit = {
    //header
    res.header.map(kv => kv._1 + ": " + kv._2).foreach(ps.println)

    ps.println()

    //body
    ps.write(res.body)
  }

  private def writeWithGZIP(ps: PrintStream, res: HttpResponse): Unit = {
    //header
    (res.header + ("Content-Encoding" -> "gzip"))
      .map(kv => kv._1 + ": " + kv._2)
      .foreach(ps.println)

    ps.println()

    //body
    using(new GZIPOutputStream(ps)) { os =>
      os.write(res.body)
    }
  }

  private def writeWithDEFLATE(ps: PrintStream, res: HttpResponse): Unit = {
    //header
    (res.header + ("Content-Encoding" -> "deflate"))
      .map(kv => kv._1 + ": " + kv._2).foreach(ps.println)

    ps.println()

    //body
    val deflator = new Deflater()
    deflator.setInput(res.body)
    deflator.finish()
    val buf = new Array[Byte](1024)
    while (!deflator.finished()) {
      deflator.deflate(buf)
      ps.write(buf)
    }
  }

  private def parseRequest(isr: InputStreamReader): Either[Status, HttpRequest] = {
    allCatch either readRequest(isr) match {
      case Left(ex) =>
        println(s"failed in reading request. ${ex.getMessage}")
        Left(BadRequest)

      case Right(request) =>
        request._1 match {
          case Some(rReq(method, path, _, ver)) =>
            method.toUpperCase match {
              case "GET" =>
                println(s"GET: $path")
                Right(HttpRequest((GET, path, HttpVersion(ver)), request._2))
              case "POST" =>
                println(s"POST: $path")
                Right(HttpRequest((POST, path, HttpVersion(ver)), request._2, request._3))
              case "PUT" =>
                println(s"PUT: $path")
                Right(HttpRequest((PUT, path, HttpVersion(ver)), request._2, request._3))
              case "DELETE" =>
                println(s"DELETE: $path")
                Right(HttpRequest((DELETE, path, HttpVersion(ver)), request._2))
              case "HEAD" =>
                println(s"HEAD: $path")
                Right(HttpRequest((HEAD, path, HttpVersion(ver)), request._2, request._3))
              case "OPTIONS" =>
                println(s"OPTIONS: $path")
                Right(HttpRequest((OPTIONS, path, HttpVersion(ver)), request._2, request._3))
              case "TRACE" =>
                println(s"TRACE: $path")
                Right(HttpRequest((TRACE, path, HttpVersion(ver)), request._2, request._3))
              case "CONNECT" =>
                println(s"CONNECT: $path")
                Right(HttpRequest((CONNECT, path, HttpVersion(ver)), request._2, request._3))
              case _ =>
                println("found unexpected method")
                Left(BadRequest)
            }
          case _ =>
            println("request didn't match")
            Left(NotFound)
        }
    }
  }

  private def readRequest(isr: InputStreamReader)
  : (Option[String], Map[String, String], Map[String, String]) = {
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
      Option(request) map (URLDecoder.decode(_, "utf-8")),
      //header
      header,
      //body if exists
      if (header.contains("Content-Length") && header.contains("Content-Type")) {
        val body = {
          val bodyArr = new Array[Char](header("Content-Length").toInt)
          br.read(bodyArr)
          bodyArr.mkString
        }

        header("Content-Type") match {
          case t if t.startsWith("application/x-www-form-urlencoded") =>
            body.split('&').map(kv => {
              val kvs = kv.split('=').map(URLDecoder.decode(_, "utf-8"))
              kvs(0) -> kvs(1)
            }).toMap
          case t if t.startsWith("multipart/form-data") =>
            println(URLDecoder.decode(body, "utf-8"))
            ???
          case _ =>
            throw new Exception("found unexpected content type")
        }
      } else Map()
      )
  }
}

