package simplehttpserver.util

import java.io._

import simplehttpserver.impl._
import simplehttpserver.util.Implicit._

import scala.io.Source
import scala.sys.process.BasicIO

object Util {
  def using[T <: {def close()}, U](resources: T)(func: T => U) = {
    try {
      func(resources)
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    } finally {
      if (resources != null)
        resources.close()
    }
  }

  class Loan[T <: {def close()}] private(resources: T) {
    def map[U](func: T => U): Option[U] =
      try {
        Option(func(resources))
      } catch {
        case ex: Throwable =>
          ex.printStackTrace()
          None
      } finally {
        if (resources != null)
          resources.close()
      }

    def flatMap[U](func: T => Option[U]): Option[U] =
      try {
        func(resources)
      } catch {
        case ex: Throwable =>
          ex.printStackTrace()
          None
      } finally {
        if (resources != null)
          resources.close()
      }
  }

  object Loan {
    def apply[T <: {def close()}](value: T) = new Loan(value)
  }

  def getContentType(str: String): ContentType = str.dropWhile(_ == '.') match {
    case "htm" | "html" =>
      html
    case "css" =>
      css
    case "ico" =>
      ico
    case "js" =>
      js
    case "jpg" | "jpeg" =>
      jpeg
    case "png" =>
      png
    case "gif" =>
      gif
    case "pdf" =>
      pdf
    case "zip" =>
      zip
    case "exe" =>
      exe
    case "txt" | _ =>
      txt
  }

  def getStringFromResources(name: String): Option[String] = {
    val cl = getClass.getClassLoader
    for {
      is <- Loan(cl.getResourceAsStream(name))
      isr <- Loan(new InputStreamReader(is))
      br <- Loan(new BufferedReader(isr))
    } yield {
      Iterator.continually(br.readLine()).takeWhile(_ != null).mkString
    }
  }

  def getStringFromFile(path: String): Option[String] = {
    val fullPath = path
    val file = new File(fullPath)

    try {
      if (file.exists && file.isFile)
        Some(Source.fromFile(file).getLines().mkString)
      else
        None
    } catch {
      case _: Throwable =>
        None
    }
  }

  def getByteArrayFromFile(file: File): Array[Byte] = {
    try {
      val out = new ByteArrayOutputStream()
      BasicIO.transferFully(new FileInputStream(file), out)
      out.toByteArray
    } catch {
      case _: Throwable =>
        Array()
    }
  }

  //publicにあればそれを, なければResourcesからファイルを探し, その文字列を返します
  def emitResponseFromFile(req: HttpRequest)
                          (status: Status, name: String): HttpResponse = {
    val bodyOpt =
      getStringFromFile("./public/" + name) match {
        case None => getStringFromResources(name)
        case bodyOpt => bodyOpt
      }

    bodyOpt match {
      case Some(body) =>
        val contentType = {
          val ext = name.split('.').lastOption getOrElse ""
          "Content-Type" -> getContentType(ext).contentType
        }
        HttpResponse(req)(status, Map(contentType), body)
      case None =>
        HttpResponse(req)(status, body = "")
    }
  }
}
