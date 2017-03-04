package com.kindone.infinitewall.persistence.wsstorage.sockets

/**
 * Created by kindone on 2017. 2. 22..
 */

trait StateContext {
  def changeState(newState: PersistentWebSocketState): Unit

  def connect(): Unit

  def scheduleReconnect(timeoutStep: Int): Unit

  def cancelOpenTimeout(): Unit

  def cancelReconnect(): Unit
}

trait PersistentWebSocketState {
  val context: StateContext

  def tryConnect(): Unit

  def closed(): Unit

  def fail(): Unit

  def timeout(): Unit

  def succeed(): Unit

  def changeState(newState: PersistentWebSocketState): Unit = {
    context.changeState(newState)
  }
}

class Initial(val context: StateContext) extends PersistentWebSocketState {
  def tryConnect(): Unit = {
    changeState(new Connecting(context))
    context.connect()
  }

  def closed(): Unit = {}

  def fail(): Unit = {}

  def timeout(): Unit = {}

  def succeed(): Unit = {}
}

class Connecting(val context: StateContext) extends PersistentWebSocketState {
  def tryConnect(): Unit = {}

  def closed(): Unit = {}

  def fail(): Unit = {
    changeState(new ConnectionRetrying(context, 1))
    context.scheduleReconnect(1)
  }

  def timeout(): Unit = {
    changeState(new ConnectionRetrying(context, 0))
    context.scheduleReconnect(0)
  }

  def succeed(): Unit = {
    changeState(new Connected(context))
    context.cancelOpenTimeout()
    context.cancelReconnect()
  }
}

class ConnectionClosed(val context: StateContext) extends PersistentWebSocketState {
  def tryConnect(): Unit = {
    changeState(new Connecting(context))
    context.connect()
  }

  def closed(): Unit = {}

  def fail(): Unit = {}

  def timeout(): Unit = {}

  def succeed(): Unit = {}
}

class ConnectionRetrying(val context: StateContext, numRetry: Int) extends PersistentWebSocketState {
  def tryConnect(): Unit = {}

  def closed(): Unit = {}

  def fail(): Unit = {
    changeState(new ConnectionRetrying(context, numRetry + 1))
    context.scheduleReconnect(numRetry)
  }

  def timeout(): Unit = {
    changeState(new ConnectionRetrying(context, numRetry + 1))
    context.scheduleReconnect(numRetry)
  }

  def succeed(): Unit = {
    changeState(new Connected(context))
    context.cancelOpenTimeout()
    context.cancelReconnect()
  }
}

class Connected(val context: StateContext) extends PersistentWebSocketState {
  def tryConnect(): Unit = {}

  def closed(): Unit = {
    changeState(new ConnectionClosed(context))
  }

  def fail(): Unit = {}

  def timeout(): Unit = {}

  def succeed(): Unit = {}
}