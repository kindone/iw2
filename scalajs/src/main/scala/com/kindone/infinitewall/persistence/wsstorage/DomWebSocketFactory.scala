package com.kindone.infinitewall.persistence.wsstorage

import com.kindone.infinitewall.persistence.wsstorage.sockets.WebSocket
import org.scalajs.dom.raw

/**
 * Created by kindone on 2017. 2. 12..
 */

trait WebSocketFactory {
  def create(url: String): WebSocket
}

class DomWebSocketFactory extends WebSocketFactory {
  def create(url: String) = {
    new WebSocket(new raw.WebSocket(url))
  }
}
