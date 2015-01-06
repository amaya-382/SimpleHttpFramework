package simplehttpserver.util

import org.json4s.native.Serialization.{read, write}

import simplehttpserver.impl.HttpSession
import simplehttpserver.util.Common._

object UseSession extends UseResources {
  private val path2SessionData = "./private/session.json"

  def getSessionBySessionId(sessionId: String): Option[HttpSession] = {
    getAllSessions find (_.sessionId == sessionId)
  }

  def getSessionById(id: String): Option[HttpSession] = {
    getAllSessions find (_.id == id)
  }

  def getAllSessions: List[HttpSession] = {
    getStringFromFile(path2SessionData) match {
      case Some(json) => read[List[HttpSession]](json)
      case None => throw new Exception("route file not found")
    }
  }

  def createNewSession(seed: String, boundId: String): HttpSession = {
    val sessionId = Security.hashBySHA384(seed)
    val newSession = HttpSession(sessionId, boundId, None, Map())
    val updated = getAllSessions :+ newSession

    writeWithResult(path2SessionData)(pw =>
      pw.print(write(updated)))(ex => {
      println(ex)
      throw new Exception("session data not found")
    })

    newSession
  }

  def deleteSession(sessionId: String): Boolean = {
    val sessions = getAllSessions
    val newSessions = sessions filter (_.sessionId != sessionId)
    val updated = sessions != newSessions

    if (updated)
      writeWithResult(path2SessionData)(pw =>
        pw.print(write(newSessions)))(ex => {
        println(ex)
        throw new Exception("session data not found")
      })

    updated
  }

}
