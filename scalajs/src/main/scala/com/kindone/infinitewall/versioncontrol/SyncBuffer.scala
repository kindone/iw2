package com.kindone.infinitewall.versioncontrol

import com.kindone.infinitewall.data.action.{ WriteAction, Action }
import com.kindone.infinitewall.data.versioncontrol.Change

/**
 * Created by kindone on 2016. 10. 29..
 */
trait SyncBuffer {
  def append(action: Action): Change
  def getSeq(): Seq[WriteAction]
}
