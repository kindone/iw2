package com.kindone.infinitewall.persistence.wsstorage.sockets

import com.kindone.infinitewall.data.action.Action
import com.kindone.infinitewall.data.versioncontrol.{ Change, Read }
import com.kindone.infinitewall.data.ws._
import com.kindone.infinitewall.events.EventListener
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent
import com.kindone.infinitewall.persistence.wsstorage.events.{ SocketEventDispatcher, SocketOpenCloseEvent }
import com.kindone.infinitewall.versioncontrol.Synchronizer
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.timers.SetTimeoutHandle

/**
 * Created by kindone on 2016. 10. 24..
 *
 * Bind socket with advance features:
 *   1. buffered changes (resend on reconnect)
 *  2. merge on conflicting changes
 *
 */
class MailboxSocket(baseUrl: String)
    extends SocketEventDispatcher {
  private val persistentWebSocket = new PersistentWebSocket(baseUrl)
  private var timeoutHandle: Option[SetTimeoutHandle] = None
  private case class Pending[T](reqId: Long, msg: T)
  private var pendingRecords = Map[Long, Pending[_]]()

  private def expire(): Unit = {
    sendPending()
  }

  private def restartTimer() = {
    timeoutHandle.foreach(js.timers.clearTimeout(_))
    timeoutHandle = Some(js.timers.setTimeout(1000) {
      expire()
    })
  }

  private def isSendScheduled = pendingRecords.size != 1

  private var maxReqId: Long = 0

  private def nextReqId() = {
    maxReqId = maxReqId + 1
    maxReqId
  }

  def setMailbox(): Unit = {

  }

  def send(action: Action): Unit = {
    val reqId = nextReqId()
    pendingRecords = pendingRecords + (reqId -> Pending[Action](reqId, action))
    val request: ClientToServerMessage = action match {
      case a: Read   => ReadRequest(reqId, a)
      case a: Change => ChangeRequest(reqId, a)
    }

    // TODO:
    // 1. if pending message exists and timeout is active, do not send immediately
    // 2. otherwise, send the message immediately
    if (!isSendScheduled)
      sendPending()
  }

  def sendPending(): Unit = {
    // TODO: send all pending messages
  }

  private def receive(responseStr: String) = {
    println("ws event:" + responseStr)

    read[ServerToClientMessage](responseStr) match {
      case Response(reqId, logId, message) =>
        pendingRecords = pendingRecords - reqId
      case Notification(logId, change @ Change(action: WallAlterAction, _, _)) =>
        dispatchWallNotificationEvent(action.wallId, new PersistenceUpdateEvent(logId, change))
      case Notification(logId, change @ Change(action: SheetAlterAction, _, _)) =>
        dispatchSheetNotificationEvent(action.sheetId, new PersistenceUpdateEvent(logId, change))
      case _ =>
        println("warning - unsupported message")
    }
  }

  //  type OnReceive = (String, Promise[_]) => Unit
  //  private case class Record[T](reqId: Long, promise: Promise[T], onReceive: OnReceive)
  //
  //  private var pendingRecords = Map[Long, Record[_]]()
  //
  //  private val wsPromise = Promise[dom.WebSocket]()
  //  private val wsFuture = wsPromise.future
  //
  //  private val ws = new dom.WebSocket("ws://" + baseUrl + "/ws")
  //  ws.onmessage = (evt: MessageEvent) => receive(evt.data.toString)
  //  ws.onopen = (evt: Event) => {
  //    wsPromise success ws
  //    dispatchSocketOpenEvent()
  //  }
  //  ws.onclose = (evt: Event) => {
  //    dispatchSocketCloseEvent()
  //  }
  //
  //  // request/response id to distinguish concurrent requests
  //  private var maxReqId: Long = 0
  //
  //  private def nextReqId() = {
  //    maxReqId = maxReqId + 1
  //    maxReqId
  //  }
  //
  //  def send[T: Reader](action: VersionedAction): Future[T] = {
  //    // sets the value for the given future
  //    val onReceiveBlock: (String, Promise[_]) => Unit = { (str: String, promise: Promise[_]) =>
  //      promise.asInstanceOf[Promise[T]] success read[T](str)
  //    }
  //    val promise = Promise[T]()
  //    val reqId = nextReqId()
  //    pendingRecords = pendingRecords + (reqId -> Record[T](reqId, promise, onReceiveBlock))
  //
  //    val request: ClientToServerMessage = action match {
  //      case a: Read   => ReadRequest(reqId, a)
  //      case a: Change => ChangeRequest(reqId, a)
  //    }
  //
  //    // send actually here
  //    // use future in order to prevent messing up w/ uninitialized state
  //    for (ws <- wsFuture) {
  //      val msg = write(request)
  //      println("ws send: " + msg)
  //      ws.send(msg)
  //    }
  //
  //    // TODO: add timeout to future
  //    //lazy val t = after(duration = 1 second, using = system.scheduler)(Future.failed(new TimeoutException("Future timed out!")))
  //
  //    promise.future
  //  }
  //
  //  private def receive(responseStr: String) = {
  //    println("ws event:" + responseStr)
  //
  //    read[ServerToClientMessage](responseStr) match {
  //      case Response(reqId, logId, message) =>
  //        for (record <- pendingRecords.get(reqId)) {
  //          record.onReceive(message, record.promise)
  //        }
  //        pendingRecords = pendingRecords - reqId
  //      case Notification(logId, change @ Change(action: WallAlterAction, _, _)) =>
  //        dispatchWallNotificationEvent(action.wallId, new PersistenceUpdateEvent(logId, change))
  //      case Notification(logId, change @ Change(action: SheetAlterAction, _, _)) =>
  //        dispatchSheetNotificationEvent(action.sheetId, new PersistenceUpdateEvent(logId, change))
  //      case _ =>
  //        println("warning - unsupported message")
  //    }
  //  }
}
