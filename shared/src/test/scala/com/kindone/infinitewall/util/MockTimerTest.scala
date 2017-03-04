package com.kindone.infinitewall.util

import org.scalatest._


/**
 * Created by kindone on 2017. 2. 19..
 */
class MockTimerTest extends FlatSpec with Matchers{
  "MockTimer" should "work as expected" in {
    val timer = new MockTimer

    var (fired1, fired2, fired3, fired4) = (false, false, false, false)

    timer.setTimeout(1000) {
      fired2 = true
      println("fired2")
    }

    timer.setTimeout(500) {
      fired1 = true
      println("fired1")
    }

    timer.setTimeout(2000) {
      fired3 = true
      println("fired3")
    }

    timer.advance(1000)
    fired1 should be (true)
    fired2 should be (true)
    fired3 should be (false)

    timer.setTimeout(500) {
      fired4 = true
      println("fired4")
    }

    timer.advance(1000)
    fired3 should be (true)
    fired4 should be (true)

  }


  "MockTimer" should "cancel as expected" in {
    val timer = new MockTimer

    var (fired1, fired2, fired3, fired4) = (false, false, false, false)

    val t2 = timer.setTimeout(1000) {
      fired2 = true
      println("fired2")
    }

    val t1 = timer.setTimeout(500) {
      fired1 = true
      println("fired1")
    }

    val t3 = timer.setTimeout(2000) {
      fired3 = true
      println("fired3")
    }

    timer.clearTimeout(t1)
    timer.advance(1000)
    fired1 should be (false)
    fired2 should be (true)
    fired3 should be (false)

    timer.setTimeout(500) {
      fired4 = true
      println("fired4")
    }

    timer.advance(1000)
    fired3 should be (true)
    fired4 should be (true)

  }
}
