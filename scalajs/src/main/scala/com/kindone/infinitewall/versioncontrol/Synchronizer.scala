package com.kindone.infinitewall.versioncontrol

import com.kindone.infinitewall.data.action.{ WriteAction, ReadonlyAction }
import com.kindone.infinitewall.data.versioncontrol.{ Change, Read }

/**
 * Created by kindone on 2016. 10. 30..
 */
trait Synchronizer extends SyncBuffer with SyncMerger {

  def createRead(action: ReadonlyAction, baseLogId: Long = 0) = {
    Read(action, baseLogId)
  }

  def createChange(action: WriteAction, baseLogId: Long = 0) = {
    Change(action, baseLogId, branch)
  }
}
