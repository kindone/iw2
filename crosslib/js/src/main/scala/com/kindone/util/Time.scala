package com.kindone.util

/**
 * Created by kindone on 2016. 9. 23..
 */
object Time {
  def now(): Long = {
    scalajs.js.Date.now().asInstanceOf[Long]
  }
}

