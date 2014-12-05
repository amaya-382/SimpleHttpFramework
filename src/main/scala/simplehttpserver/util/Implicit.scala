package simplehttpserver.util


object Implicit {
  implicit def string2BytesArray(str: String): Array[Byte] =
    str.getBytes
}
