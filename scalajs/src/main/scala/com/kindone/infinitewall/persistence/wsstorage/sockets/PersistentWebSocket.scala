package com.kindone.infinitewall.persistence.wsstorage.sockets

import com.kindone.infinitewall.persistence.wsstorage.events.{ MessageReceiveEventEventDispatcher, SocketOpenCloseEventDispatcher }
import org.scalajs.dom
import org.scalajs.dom.raw.{ Event, MessageEvent }
import upickle.default._

import scala.scalajs.js.JavaScriptException
import scala.scalajs.js.timers._

/**
 * Created by kindone on 2016. 4. 17..
 */
class PersistentWebSocket(baseUrl: String) extends SocketOpenCloseEventDispatcher
    with MessageReceiveEventEventDispatcher {

  private var socket: Option[dom.WebSocket] = connect(baseUrl)
  private var isConnected = false
  private var backOff = 0

  private val onMessage = (evt: MessageEvent) => {
    dom.console.info("WebSocket onMessage event called: " + evt.toString)
    dispatchReceiveEvent(evt.data.toString)
  }

  private val onOpen = (evt: Event) => {
    dom.console.info("WebSocket onOpen event called: " + evt.toString)
    isConnected = true
    backOff = 0
    dispatchSocketOpenEvent()
  }

  private val onClose = (evt: Event) => {
    dom.console.info("WebSocket onClose event called: " + evt.toString)
    isConnected = false
    dispatchSocketCloseEvent()

    reconnect()
  }

  private val onError = (evt: Event) => {
    println("Error occurred in WebSocket: " + evt.toString)
  }

  private def connect(baseUrl: String): Option[dom.WebSocket] = {
    try {
      val ws = new dom.WebSocket("ws://" + baseUrl + "/ws")
      ws.onmessage = onMessage
      ws.onopen = onOpen
      ws.onclose = onClose
      ws.onerror = onError
      Some(ws)
    } catch {
      case err: JavaScriptException =>
        dom.console.error("Error occurred in creating WebSocket object: " + err.toString())
        backOff += 1
        reconnect()
        None
    }
  }

  private def close(): Unit = {
    if (isConnected) {
      for (s <- socket) {
        dom.console.info("Closing existing WebSocket")
        s.close()
        s.onmessage = null
        s.onopen = null
        s.onclose = null
        s.onerror = null
      }
    }
  }

  private def reconnect(): Unit = {
    close()

    dom.console.info(s"Trying WebSocket reconnection(${backOff}) ... ")

    runWithBackOff {
      socket = connect(baseUrl)
    }
    if (backOff < 5)
      backOff += 1
  }

  private def runWithBackOff(block: => Unit): Unit = {
    if (backOff > 0) {
      setTimeout(Math.pow(2.0, backOff) * 500) {
        block
      }
    } else
      block
  }

  def send[T: Reader](str: String): Unit = {
    socket.foreach(_.send(str))
  }
}
