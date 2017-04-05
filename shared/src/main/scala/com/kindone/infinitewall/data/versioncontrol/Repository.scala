package com.kindone.infinitewall.data.versioncontrol

import com.kindone.infinitewall.data.state.State

/**
 * Created by kindone on 2016. 10. 2..
 */
class Repository(baseState:State) {
  private val history:History = new History(baseState)

  def append(change:Change):Unit = {
    history.append(change)
  }

  def rebase(newChanges:Seq[Change]):Unit = {
    history.rebase(newChanges)
  }

}
