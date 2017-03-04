package com.kindone.infinitewall.persistence.wsstorage

import com.kindone.infinitewall.data.action.Action
import com.kindone.infinitewall.data.versioncontrol.{ Branch, Change }
import com.kindone.infinitewall.data.ws.{ Notification, Response, ServerToClientMessage, ChangeRequest }
import com.kindone.infinitewall.events._
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent
import com.kindone.infinitewall.persistence.wsstorage.sockets.{ MailboxWebSocket, MailboxSocket }
import com.kindone.infinitewall.util.SimpleIdGenerator
import upickle.default._

import scala.concurrent.{ Promise, Future }

/**
 * Created by kindone on 2016. 12. 10..
 */
class MessageProcessor(branch: Branch, socket: MailboxSocket) {
  var messages: Map[Long, Action] = Map()
  var baseLogId = 0L
  val requestProcessor = new RequestResponseProcessor

  def send[T: Reader](action: Action): Future[T] = {
    val reqId = requestProcessor.getNextRequestId()
    messages = messages + (reqId -> action)
    requestProcessor.getResponseFuture[T](reqId)
  }

  def updateMailbox() = {

    // convert to network friendly string
    val convertedMessages: List[String] =
      for (message <- messages.toList) yield {
        val (reqId, action) = message
        write(ChangeRequest(reqId, Change(action, baseLogId, branch)))
      }

    socket.setMailbox(convertedMessages)
  }

  def addOnSheetNotificationListener(sheetId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    // TODO
  }

  def addOnWallNotificationListener(wallId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    // TODO
  }

  private def onReceive(msg: String) = {

    val parsedMessage = read[ServerToClientMessage](msg)
    parsedMessage match {
      case Response(reqId, logId, message) =>
        requestProcessor.processResponse(reqId, message)
      case notification: Notification =>
    }
  }
}
