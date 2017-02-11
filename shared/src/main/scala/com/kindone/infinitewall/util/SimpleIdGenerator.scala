package com.kindone.infinitewall.util

/**
 * Created by kindone on 2016. 12. 11..
 */
class SimpleIdGenerator {
  private var id = 0L

  def getNextId() = {
    id = id + 1
    id
  }
}
