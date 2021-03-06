package com.kindone.infinitewall.persistence.wsstorage

import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.ws.{ Notification, ServerToClientMessage, Request, Response }
import com.kindone.infinitewall.events.{ EventListener, EventDispatcher }
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent
import com.kindone.infinitewall.persistence.wsstorage.events.{ WebSocketEventDispatcher, WebSocketEvent }

import org.scalajs.dom.raw.{ MessageEvent, Event }

import scala.concurrent.{ Future, Promise }
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import upickle.default._
import org.scalajs.dom

/**
 * Created by kindone on 2016. 4. 17..
 */
class Socket(baseUrl: String) extends WebSocketEventDispatcher {

  type OnReceive = (String, Promise[_]) => Unit
  case class Record[T](id: Long, promise: Promise[T], onReceive: OnReceive)

  var map = Map[Long, Record[_]]()

  val wsPromise = Promise[dom.WebSocket]()
  val wsFuture = wsPromise.future

  val ws = new dom.WebSocket("ws://" + baseUrl + "/ws")
  ws.onmessage = (evt: MessageEvent) => receive(evt.data.toString)
  ws.onopen = (evt: Event) => {
    wsPromise success ws
    dispatchSocketOpenEvent()
  }
  ws.onclose = (evt: Event) => {
    dispatchSocketCloseEvent()
  }

  // request/response id to distinguish concurrent requests
  private var maxId: Long = 0

  def nextId() = {
    maxId = maxId + 1
    maxId
  }

  def send[T: Reader](action: Action) = {
    val block: (String, Promise[_]) => Unit = { (str: String, promise: Promise[_]) =>
      promise.asInstanceOf[Promise[T]] success read[T](str)
    }
    val promise = Promise[T]()
    val msgId = nextId
    map = map + (msgId -> Record[T](msgId, promise, block))

    // send actually here
    for (ws <- wsFuture) {
      val msg = write(Request(msgId, action))
      println("ws send: " + msg)
      ws.send(msg)
    }

    promise.future
  }

  def receive(responseStr: String) = {
    println("ws event:" + responseStr)
    read[ServerToClientMessage](responseStr) match {
      case Response(id, logId, message) =>
        for (record <- map.get(id)) {
          record.onReceive(message, record.promise)
        }
        map = map - id
      case Notification(msgId, action: WallAlterAction) =>
        dispatchWallNotificationEvent(action.wallId, new PersistenceUpdateEvent(action))
      case Notification(msgId, action: SheetAlterAction) =>
        dispatchSheetNotificationEvent(action.sheetId, new PersistenceUpdateEvent(action))
      case _ =>
      // TODO emit unsupported
    }
  }
}
