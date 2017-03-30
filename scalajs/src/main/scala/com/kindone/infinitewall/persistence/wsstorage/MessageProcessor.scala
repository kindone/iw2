package com.kindone.infinitewall.persistence.wsstorage

import com.kindone.infinitewall.data.action.{ SheetAction, WallAction, Action }
import com.kindone.infinitewall.data.versioncontrol.{ Branch, Change }
import com.kindone.infinitewall.data.ws.{ Notification, Response, ServerToClientMessage, ChangeRequest }
import com.kindone.infinitewall.events._
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent
import com.kindone.infinitewall.persistence.wsstorage.events.MessageReceiveEvent
import com.kindone.infinitewall.persistence.wsstorage.sockets.{ MailboxWebSocket, MailboxSocket }
import com.kindone.infinitewall.util.SimpleIdGenerator
import upickle.default._

import scala.concurrent.{ Promise, Future }

/**
 * Created by kindone on 2016. 12. 10..
 */
class MessageProcessor(branch: Branch, socket: MailboxSocket) {
  private var messages: Map[Long, Action] = Map()
  private var baseLogId = 0L
  private val requestHandler = new RequestResponseHandler
  private var sheetNotificationListeners: List[EventListener[PersistenceUpdateEvent]] = List()
  private var wallNotificationListeners: List[EventListener[PersistenceUpdateEvent]] = List()

  socket.addOnReceiveListener((e: MessageReceiveEvent) => {
    onReceive(e.str)
  })

  def size = messages.size

  def send[T: Reader](action: Action, baseLogId: Long = 0): Future[T] = {
    val reqId = requestHandler.getNextRequestId()
    messages = messages + (reqId -> action)
    updateMailbox(baseLogId)
    requestHandler.getResponseFuture[T](reqId)
  }

  def addOnSheetNotificationListener(sheetId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    sheetNotificationListeners :+= handler
  }

  def addOnWallNotificationListener(wallId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    wallNotificationListeners :+= handler
  }

  private def updateMailbox(baseLogId: Long) = {
    // convert to network friendly string
    val convertedMessages: List[String] =
      for (message <- messages.toList) yield {
        val (reqId, action) = message
        this.baseLogId = baseLogId
        write(ChangeRequest(reqId, Change(action, baseLogId, branch)))
      }

    socket.setMailbox(convertedMessages)
  }

  private def onReceive(msg: String) = {

    val parsedMessage = read[ServerToClientMessage](msg)
    parsedMessage match {
      case Response(reqId, logId, message) =>
        requestHandler.processResponse(reqId, message)
        messages -= reqId
        updateMailbox(this.baseLogId)
      case Notification(logId, change) =>
        change.action match {
          case _: WallAction =>
            for (listener <- wallNotificationListeners) {
              listener(PersistenceUpdateEvent(logId, change))
            }
          case _: SheetAction =>
            for (listener <- sheetNotificationListeners) {
              listener(PersistenceUpdateEvent(logId, change))
            }
        }

    }
  }
}
