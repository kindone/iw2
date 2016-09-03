package com.kindone.infinitewall.data

import java.security.MessageDigest

import com.kindone.infinitewall.data.versioncontrol.Hasher

/**
 * Created by kindone on 2016. 8. 14..
 */
object ScalaHasher extends Hasher {

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
