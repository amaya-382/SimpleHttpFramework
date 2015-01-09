package simplehttpserver.impl

sealed abstract class ContentType {
  val ownType: String
  val contentType: String
}

case object html extends ContentType {
  val ownType = "html"
  val contentType = "text/html"
}

case object css extends ContentType {
  val ownType = "css"
  val contentType = "text/css"
}

case object ico extends ContentType {
  val ownType = "ico"
  val contentType = "image/vnd.microsoft.icon"
}

case object js extends ContentType {
  val ownType = "js"
  val contentType = "text/javascript"
}

case object jpeg extends ContentType {
  val ownType = "jpeg"
  val contentType = "image/jpeg"
}

case object png extends ContentType {
  val ownType = "png"
  val contentType = "image/png"
}

case object gif extends ContentType {
  val ownType = "gif"
  val contentType = "image/gif"
}

case object pdf extends ContentType {
  val ownType = "pdf"
  val contentType = "application/pdf"
}

case object zip extends ContentType {
  val ownType = "zip"
  val contentType = "application/zip"
}

case object exe extends ContentType {
  val ownType = "exe"
  val contentType = "application/octet-stream"
}

case object txt extends ContentType {
  val ownType = "txt"
  val contentType = "text/plain"
}
