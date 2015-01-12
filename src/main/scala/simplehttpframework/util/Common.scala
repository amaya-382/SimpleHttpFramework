package simplehttpframework.util

import java.io.PrintWriter

import simplehttpframework.impl._

import scala.util.control.NonFatal

object Common {
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
        case NonFatal(ex) =>
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
        case NonFatal(ex) =>
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
    case "svg" =>
      svg
    case "pdf" =>
      pdf
    case "zip" =>
      zip
    case "exe" =>
      exe
    case "txt" | _ =>
      txt
  }

  def byteArray2HexString(bytes: Array[Byte]): String = {
    val sb = new StringBuilder
    bytes foreach { s => sb.append(s.formatted("%02x"))}
    sb.toString()
  }

  def writeWithResult[T]
  (path: String)
  (write: PrintWriter => T)
  (fail: Throwable => T): T = {
    val pw = new PrintWriter(path)
    try {
      write(pw)
    } catch {
      case NonFatal(ex) =>
        fail(ex)
    } finally {
      pw.close()
    }
  }
}
