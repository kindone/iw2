package com.kindone.infinitewall.versioncontrol

import com.kindone.infinitewall.data.action.Action

/**
 * Created by kindone on 2016. 10. 29..
 */
class History {
  private val pendingRecords: List[Int] = List()

  def write(action: Action): Unit = {

  }
  def redo(): Unit = ???
  def undo(): Unit = ???
}
