package simplehttpframework.impl

case class HttpVersion(ver: String) extends AnyVal {
  override def toString: String = {
    "HTTP/" + ver
  }
}
