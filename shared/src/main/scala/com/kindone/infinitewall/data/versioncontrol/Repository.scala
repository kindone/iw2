package com.kindone.infinitewall.data.versioncontrol

import com.kindone.infinitewall.data.state.State

/**
 * Created by kindone on 2016. 10. 2..
 */
class Repository(baseState:State) {
  private val changeStream:ChangeStream = new ChangeStream(baseState)

  def append(change:Change):Unit = {
    changeStream.append(change)
  }

  def rebase(newChanges:Seq[Change]):Unit = {
    changeStream.rebase(newChanges)
  }

}
