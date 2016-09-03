package com.kindone.infinitewall.data

import com.kindone.infinitewall.data.versioncontrol.Hasher
import com.kindone.infinitewall.facades.CryptoJS

/**
 * Created by kindone on 2016. 8. 14..
 */
object JSHasher extends Hasher {
  lazy val crypto = new CryptoJS

  def SHA1(str: String) = {
    crypto.SHA1(str)
  }
}
