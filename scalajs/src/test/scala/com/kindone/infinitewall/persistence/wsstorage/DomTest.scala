package com.kindone.infinitewall.persistence.wsstorage

import utest._
import utest.framework.{ Test, Tree }

/**
 * Created by kindone on 2017. 2. 6..
 */
object DomTest extends TestSuite {
  val tests: Tree[Test] = this{
    'testException{
      2 //throw new Exception("test1")
    }
    'test2{
      1
    }
    'testDomTimer{
      val a = List[Byte](1, 2)
      import scala.scalajs.js
      import scala.scalajs.js.timers.SetTimeoutHandle
      js.timers.setTimeout(1000) {
        println("hello")
      }
      //      a(10)
    }
  }
}
