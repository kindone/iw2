package com.kindone.infinitewall.persistence.wsstorage.sockets

import org.scalajs.dom.raw.{ CloseEvent, MessageEvent, ErrorEvent, Event }

/**
 * Created by kindone on 2017. 3. 19..
 */
trait WebSocketInterface {
  var onopen: scalajs.js.Function1[Event, _]
  var onerror: scalajs.js.Function1[ErrorEvent, _]
  var onmessage: scalajs.js.Function1[MessageEvent, _]
  var onclose: scalajs.js.Function1[CloseEvent, _]
  def close(code: Int, reason: String): Unit
  def send(str: String): Unit

  def toString(): String
}
