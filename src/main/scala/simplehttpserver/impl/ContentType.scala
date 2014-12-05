package simplehttpserver.impl

sealed abstract class ContentType {
  def ownType: String
  def contentType: String
}

case object html extends ContentType {
  def ownType = "html"
  def contentType = "text/html"
}
case object htm extends ContentType {
  def ownType = "html"
  def contentType = "text/html"
}
case object css extends ContentType {
  def ownType = "css"
  def contentType = "text/css"
}
case object ico extends ContentType {
  def ownType = "ico"
  def contentType = "image/vnd.microsoft.icon"
}
case object js extends ContentType {
  def ownType = "js"
  def contentType = "text/javascript"
}
case object jpg extends ContentType {
  def ownType = "jpeg"
  def contentType = "image/jpeg"
}
case object jpeg extends ContentType {
  def ownType = "jpeg"
  def contentType = "image/jpeg"
}
case object png extends ContentType {
  def ownType = "png"
  def contentType = "image/png"
}
case object gif extends ContentType {
  def ownType = "gif"
  def contentType = "image/gif"
}
case object pdf extends ContentType {
  def ownType = "pdf"
  def contentType = "application/pdf"
}
case object txt extends ContentType {
  def ownType = "txt"
  def contentType = "text/plain"
}
case object zip extends ContentType {
  def ownType = "zip"
  def contentType = "application/zip"
}
case object exe extends ContentType {
  def ownType = "exe"
  def contentType = "application/octet-stream"
}
