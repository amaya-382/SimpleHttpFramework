package simplehttpserver.impl

sealed abstract class Status(val code: Int) {
  def toString: String
}

sealed abstract class Informational(override val code: Int) extends Status(code)
sealed abstract class Success(override val code: Int) extends Status(code)
sealed abstract class Redirection(override val code: Int) extends Status(code)
sealed abstract class ClientError(override val code: Int) extends Status(code)
sealed abstract class ServerError(override val code: Int) extends Status(code)

case object Continue extends Informational(100) {
  override def toString: String = "Continue"
}
case object SwitchingProtocols extends Informational(101) {
  override def toString: String = "Switching Protocols"
}
case object Processing extends Informational(102) {
  override def toString: String = "Processing"
}

case object Ok extends Success(200) {
  override def toString: String = "OK"
}
case object Created extends Success(201) {
  override def toString: String = "Created"
}
case object Accepted extends Success(202) {
  override def toString: String = "Accepted"
}
case object Non_AuthoritativeInformation extends Success(203) {
  override def toString: String = "Non-Authoritative Information"
}
case object NoContent extends Success(204) {
  override def toString: String = "No Content"
}
case object ResetContent extends Success(205) {
  override def toString: String = "Reset Content"
}
case object PartialContent extends Success(206) {
  override def toString: String = "Partial Content"
}
case object Multi_Status extends Success(207) {
  override def toString: String = "Multi-Status"
}
case object IMUsed extends Success(226) {
  override def toString: String = "IM Used"
}

case object MultipleChoices extends Redirection(300) {
  override def toString: String = "Multiple Choices"
}
case object MovedPermanently extends Redirection(301) {
  override def toString: String = "Moved Permanently"
}
case object Found extends Redirection(302) {
  override def toString: String = "Found"
}
case object SeeOther extends Redirection(303) {
  override def toString: String = "See Other"
}
case object NotModified extends Redirection(304) {
  override def toString: String = "Not Modified"
}
case object UseProxy extends Redirection(305) {
  override def toString: String = "Use Proxy"
}
case object TemporaryRedirect extends Redirection(307) {
  override def toString: String = "Temporary Redirect"
}
case object PermanentRedirect extends Redirection(308) {
  override def toString: String = "Permanent Redirect"
}

case object BadRequest extends ClientError(400) {
  override def toString: String = "Bad Request"
}
case object Unauthorized extends ClientError(401) {
  override def toString: String = "Unauthorized"
}
case object PaymentRequired extends ClientError(402) {
  override def toString: String = "Payment Required"
}
case object Forbidden extends ClientError(403) {
  override def toString: String = "Forbidden"
}
case object NotFound extends ClientError(404) {
  override def toString: String = "Not Found"
}
case object MethodNotAllowed extends ClientError(405) {
  override def toString: String = "Method Not Allowed"
}
case object NotAcceptable extends ClientError(406) {
  override def toString: String = "Not Acceptable"
}
case object ProxyAuthenticationRequired extends ClientError(407) {
  override def toString: String = "Proxy Authentication Required"
}
case object RequestTimeout extends ClientError(408) {
  override def toString: String = "Request Timeout"
}
case object Conflict extends ClientError(409) {
  override def toString: String = "Conflict"
}
case object Gone extends ClientError(410) {
  override def toString: String = "Gone"
}
case object LengthRequired extends ClientError(411) {
  override def toString: String = "Length Required"
}
case object PreconditionFailed extends ClientError(412) {
  override def toString: String = "Precondition Failed"
}
case object RequestEntityTooLarge extends ClientError(413) {
  override def toString: String = "Request Entity Too Large"
}
case object Request_URITooLong extends ClientError(414) {
  override def toString: String = "Request-URI Too Long"
}
case object UnsupportedMediaType extends ClientError(415) {
  override def toString: String = "Unsuported Media Type"
}
case object RequestedRangeNotSatisfiable extends ClientError(416) {
  override def toString: String = "Requested Range Not Satisfiable"
}
case object ExpectationFailed extends ClientError(417) {
  override def toString: String = "Exception Failed"
}
case object ImATeapot extends ClientError(418) {
  override def toString: String = "I'm a teapot"
}
case object UnprocessableEntity extends ClientError(422) {
  override def toString: String = "Unprocessable Entity"
}
case object Locked extends ClientError(423) {
  override def toString: String = "Locked"
}
case object FailedDependency extends ClientError(424) {
  override def toString: String = "Failed Dependency"
}
case object UpgradeRequired extends ClientError(426) {
  override def toString: String = "Upgrade Required"
}

case object InternalServerError extends ServerError(500) {
  override def toString: String = "Internal Server Error"
}
case object NotImplemented extends ServerError(501) {
  override def toString: String = "Not Implemented"
}
case object BadGateway extends ServerError(502) {
  override def toString: String = "Bad Gateway"
}
case object ServiceUnavailable extends ServerError(503) {
  override def toString: String = "Continue"
}
case object GatewayTimeout extends ServerError(504) {
  override def toString: String = "Service Unavailable"
}
case object HTTPVersionNotSupported extends ServerError(505) {
  override def toString: String = "HTTP Version Not Supported"
}
case object VariantAlsoNegotiates extends ServerError(506) {
  override def toString: String = "Variant Also Negotiates"
}
case object InsufficientStorage extends ServerError(507) {
  override def toString: String = "Insufficient Storage"
}
case object BandwidthLimitExceeded extends ServerError(509) {
  override def toString: String = "Bandwidth Limit Exceeded"
}
case object NotExtended extends ServerError(510) {
  override def toString: String = "Not Extended"
}
