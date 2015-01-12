package simplehttpframework.util

import java.util.Date

import org.json4s.DefaultFormats
import org.json4s.native.Serialization.{read, write}

import simplehttpframework.impl.HttpSession
import simplehttpframework.util.Common._

object UseSession extends UseResources {
  implicit private val formats = DefaultFormats
  private val path2SessionData = "./private/session.json"

  def getSessionBySessionId(sessionId: String): Option[HttpSession] = {
    getAllSessions find (_.sessionId == sessionId)
  }

  def getSessionById(id: String): Option[HttpSession] = {
    getAllSessions find (_.id == id)
  }

  def getAllSessions: Set[HttpSession] = {
    getStringFromFile(path2SessionData) match {
      case Some(json) => read[Set[HttpSession]](json)
      case None => throw new Exception("route file not found")
    }
  }

  def createNewSession(seed: String, boundId: String,
                       expires: Option[Date] = None,
                       data: Map[String, String] = Map()): HttpSession = {
    val sessionId = Security.hashBySHA384(seed)
    val newSession = HttpSession(sessionId, boundId, expires, data)
    val updated = getAllSessions + newSession

    writeWithResult(path2SessionData)(pw =>
      pw.print(write(updated)))(ex => {
      println(ex)
      throw new Exception("session data not found")
    })

    newSession
  }

  def deleteSession(sessionId: String): Option[HttpSession] = {
    val sessions = getAllSessions
    val sessionOpt = sessions find (_.sessionId == sessionId)

    sessionOpt map (
      session => {
        val newSessions = sessions - session
        writeWithResult(path2SessionData)(pw =>
          pw.print(write(newSessions)))(ex => {
          println(ex)
          throw new Exception("session data not found")
        })
        session
      })
  }
}
