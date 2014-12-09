package simplehttpserver.util

import java.io.{FileInputStream, ByteArrayOutputStream, File}

import simplehttpserver.impl._
import simplehttpserver.util.Implicit._

import scala.io.Source
import scala.sys.process.BasicIO

object Util {
  val path2Assets = "./public"
  val path2Resources = "./src/main/resources"

  def using[T <: {def close()}, U](resource: T)(func: T => U) = {
    try {
      func(resource)
    }
    catch {
      case ex: Exception => ex.printStackTrace()
    }
    finally {
      if (resource != null) resource.close()
    }
  }

  def findAsset(path: String): Option[File] = {
    val file = new File(path2Assets + path)
    if (file.exists && file.isFile) Some(file)
    else None
  }

  def getContentType(str: String): ContentType = str.dropWhile(_ == ".") match {
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

  def getStringFromFile(name: String): Option[String] = {
    val path = path2Resources + "/" + name
    val file = new File(path)
    if (file.exists() && file.isFile) {
      val src = Source.fromFile(path)
      try {
        Some(src.getLines().fold("")(_ + _))
      } catch {
        case _: Throwable =>
          None
      } finally {
        src.close()
      }
    } else None
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
                          (status: Status, path: String): HttpResponse = {
    getStringFromFile(path) match {
      case Some(body) =>
        val contentType = {
          val ext = path.split('.').lastOption getOrElse ""
          "Content-Type" -> getContentType(ext).contentType
        }
        HttpResponse(req)(status, Map(contentType), body)
      case None =>
        HttpResponse(req)(status, body = "")
    }
  }
}
