package simplehttpserver.util

import java.io.File

object Util {
  val pathToAssets = "./public"

  def using[T <: {def close()}, U](resource: T)(func: T => U) {
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
    val file = new File(pathToAssets + path)
    if (file.exists) Some(file)
    else None
  }
}
