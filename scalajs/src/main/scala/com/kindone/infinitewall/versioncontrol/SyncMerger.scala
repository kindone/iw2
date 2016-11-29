package com.kindone.infinitewall.versioncontrol

import com.kindone.infinitewall.data.versioncontrol.Change

/**
 * Created by kindone on 2016. 10. 29..
 */
trait SyncMerger {
  def rebase(changes: Seq[Change]): Unit
}
