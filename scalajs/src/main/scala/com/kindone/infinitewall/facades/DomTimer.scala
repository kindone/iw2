package com.kindone.infinitewall.facades

import java.util.UUID

import com.kindone.infinitewall.util.Timer
import scala.scalajs.js._

/**
 * Created by kindone on 2017. 2. 19..
 */
class DomTimer extends Timer {
  var UUIDToId: Map[UUID, timers.SetTimeoutHandle] = Map()

  override def setTimeout(timeoutMs: Long)(task: => Unit): UUID = {
    val id = timers.setTimeout(timeoutMs) {
      task
    }
    val uuid = UUID.randomUUID()
    UUIDToId += (uuid -> id)

    uuid
  }

  override def clearTimeout(uuid: UUID): Unit = {
    UUIDToId.get(uuid).foreach { id =>
      timers.clearTimeout(id)
    }
    UUIDToId -= uuid
  }
}
