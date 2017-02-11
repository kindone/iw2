package com.kindone.infinitewall.persistence.wsstorage.sockets

import org.scalajs.dom
import org.scalajs.dom.raw.{ Event, MessageEvent }

/**
 * Created by kindone on 2016. 12. 6..
 */
class WebSocket(val url: String, val protocol: String = "") extends Socket {

  private val ws = new dom.WebSocket(url)
  ws.onopen = onOpen _
  ws.onerror = onError _
  ws.onclose = onClose _
  ws.onmessage = onMessage _

  private def onMessage(evt: MessageEvent) = {
    dom.console.info("WebSocket onMessage event called: " + evt.toString)
    dispatchReceiveEvent(evt.data.toString)
  }

  private def onOpen(evt: Event) = {
    dom.console.info("WebSocket onOpen event called: " + evt.toString)
    dispatchSocketOpenEvent()
  }

  private def onClose(evt: Event) = {
    dom.console.info("WebSocket onClose event called: " + evt.toString)
    dispatchSocketCloseEvent()
  }

  private def onError(evt: Event) = {
    dom.console.error("Error occurred in WebSocket: " + evt.toString)
  }

  def close(): Unit = {
    ws.close()
    removeAllOnErrorListener()
    removeAllOnReceiveListener()
    removeAllOnSocketOpenCloseListener()
  }

  def send(str: String): Unit = {
    ws.send(str)
  }
}
