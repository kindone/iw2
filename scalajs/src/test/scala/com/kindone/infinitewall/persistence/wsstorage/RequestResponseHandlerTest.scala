package com.kindone.infinitewall.persistence.wsstorage

import org.scalamock.scalatest.MockFactory
import org.scalatest.{ Matchers, FunSuite }
import upickle.default._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

/**
 * Created by kindone on 2017. 3. 5..
 */
class RequestResponseHandlerTest extends FunSuite with MockFactory with Matchers {

  import scala.concurrent._
  import scala.concurrent.duration._

  test("testBasics") {
    val processor = new RequestResponseHandler

    val reqId1 = processor.getNextRequestId()
    val future1 = processor.getResponseFuture[Boolean](reqId1)
    val reqId2 = processor.getNextRequestId()
    val future2 = processor.getResponseFuture[Int](reqId2)

    var count = 2

    processor.processResponse(reqId1, write(true))
    processor.processResponse(reqId2, write(6))
    // test unregistered request id (should do nothing)
    processor.processResponse(100, write(6))

    future1.foreach { value =>
      value should be(true)
      count -= 1
      //      processor.size should be(count)
    }
    future2.foreach { value =>
      value should be(6)
      count -= 1
      //      processor.size should be(count)
    }
  }

}
