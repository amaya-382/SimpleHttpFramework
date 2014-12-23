package simplehttpserver.util.implicits

object Implicit {
  implicit def string2BytesArray(str: String): Array[Byte] =
    str.getBytes
}
