package simplehttpserver.util

import java.io._

import simplehttpserver.impl._
import simplehttpserver.util.Implicit._

import scala.sys.process.BasicIO

object Util {
  val path2Assets = "./public"

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

  def findAsset(path: String): Option[File] = {
    val file = new File(path2Assets + path)
    if (file.exists && file.isFile) Some(file)
    else None
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

  def emitResponseFromFile(req: HttpRequest)
                          (status: Status, name: String): HttpResponse = {
    getStringFromResources(name) match {
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
