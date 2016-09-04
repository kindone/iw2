package com.kindone.util

import java.security.MessageDigest

/**
 * Created by kindone on 2016. 1. 24..
 */

object Hasher {
  def SHA1(input: String): String = {
    val mDigest: MessageDigest = MessageDigest.getInstance("SHA1")
    val result: Array[Byte] = mDigest.digest(input.getBytes)
    val sb = new StringBuffer()
    for (byte <- result) {
      sb.append(Integer.toString((byte & 0xff) + 0x100, 16).substring(1))
    }

    sb.toString
  }
}