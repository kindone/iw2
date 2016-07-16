package com.kindone.infinitewall.data.ws

import com.kindone.infinitewall.data.action.Action

/**
 * Created by kindone on 2016. 4. 23..
 */
case class Request(reqId: Long, action:Action)

sealed trait ServerToClientMessage
case class Response(reqId: Long, logId: Long, message: String) extends ServerToClientMessage
case class Notification(logId: Long, action: Action) extends ServerToClientMessage
