package com.kindone.infinitewall.data.communication

import com.kindone.infinitewall.data.action.{ReadonlyAction, Action}
import com.kindone.infinitewall.data.versioncontrol.{ Read, Change}

/**
 * Created by kindone on 2016. 4. 23..
 */
sealed abstract class ClientToServerMessage {
  def reqId: Long
}

sealed trait ServerToClientMessage

case class ChangeRequest(reqId: Long, change:Change) extends ClientToServerMessage
case class ReadRequest(reqId: Long, read:Read) extends ClientToServerMessage

case class Response(reqId: Long, logId: Long, message: String) extends ServerToClientMessage
case class Notification(logId: Long, change: Change) extends ServerToClientMessage
