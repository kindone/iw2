package com.kindone.util

import scala.scalajs.js

/**
 * Created by kindone on 2016. 1. 24..
 */
@js.native
object CryptoJS extends js.Object {
  def SHA1(text: String): String = js.native
}

object Hasher {
  def SHA1(input: String): String = {
    CryptoJS.SHA1(input)
  }
}