package com.kindone.infinitewall.persistence.wsstorage

import com.kindone.infinitewall.util.SimpleIdGenerator
import upickle.default._

import scala.concurrent.{ Future, Promise }

/**
 * Created by kindone on 2016. 12. 11..
 */
class RequestResponseProcessor {

  type OnReceive = (String, Promise[_]) => Unit
  private case class Record[T](reqId: Long, promise: Promise[T], onReceive: OnReceive)

  private var requestIdGenerator = new SimpleIdGenerator
  private var pendingRecords = Map[Long, Record[_]]()

  def getNextRequestId() = requestIdGenerator.getNextId()

  def getResponseFuture[T: Reader](reqId: Long): Future[T] = {
    val onReceiveBlock: (String, Promise[_]) => Unit = { (str: String, promise: Promise[_]) =>
      promise.asInstanceOf[Promise[T]] success read[T](str)
    }
    val promise = Promise[T]()
    pendingRecords = pendingRecords + (reqId -> Record[T](reqId, promise, onReceiveBlock))
    promise.future
  }

  def processResponse(reqId: Long, message: String): Unit = {
    pendingRecords.get(reqId).foreach(record => record.onReceive(message, record.promise))
    pendingRecords = pendingRecords - reqId
  }

}
