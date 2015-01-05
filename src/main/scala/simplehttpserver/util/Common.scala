package simplehttpserver.util

import simplehttpserver.impl._

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
}
