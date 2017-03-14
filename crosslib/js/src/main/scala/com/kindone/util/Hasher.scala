package com.kindone.util

import scala.scalajs.js
import org.scalajs.dom.crypto._

/**
 * Created by kindone on 2016. 1. 24..
 */
@js.native
object CryptoJS extends js.Object {
  def SHA1(text: String): String = js.native
}

object Hasher {
  def SHA1(input: String): String = {
    //    CryptoJS.SHA1(input)
    scala.scalajs.js.Dynamic.global.sha1.create().update(input).hex().asInstanceOf[String]
  }
}