package com.kindone.infinitewall.persistence.wsstorage.sockets

import org.scalajs.dom
import org.scalajs.dom.raw.{ ErrorEvent, Event, MessageEvent }
import upickle.default._

/**
 * Created by kindone on 2016. 12. 6..
 */
class WebSocket(val ws: WebSocketInterface) extends Socket {

  val self = this

  ws.onopen = (e: Event) => self.onOpen(e)
  ws.onerror = (e: ErrorEvent) => self.onError(e)
  ws.onclose = (e: Event) => self.onClose(e)
  ws.onmessage = (e: MessageEvent) => self.onMessage(e)

  private def onMessage(evt: MessageEvent) = {
    dom.console.info("WebSocket onMessage event called: " + evt.data)
    dispatchReceiveEvent(evt.data.toString)
  }

  private def onOpen(evt: Event) = {
    dom.console.info("WebSocket onOpen event called: " + self + ":" + ws.toString + ":" + numSocketCloseEventListeners + ":" + evt.toString)
    dispatchSocketOpenEvent()
  }

  private def onClose(evt: Event) = {
    dom.console.info("WebSocket onClose event called: " + evt.toString)
    dispatchSocketCloseEvent()
  }

  private def onError(evt: ErrorEvent) = {
    dom.console.error("Error occurred in WebSocket: " + evt.message)
    dispatchErrorEvent(evt.message)
  }

  def close(): Unit = {
    dom.console.info("WebSocket::close()")
    ws.close(1000, "normal")
    removeAllOnErrorListener()
    removeAllOnReceiveListener()
    removeAllOnSocketOpenCloseListener()
  }

  def send(str: String): Unit = {
    ws.send(str)
  }
}
