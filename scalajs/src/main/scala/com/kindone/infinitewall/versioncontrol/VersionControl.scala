package com.kindone.infinitewall.versioncontrol

import com.kindone.infinitewall.data.action.{ _ }
import com.kindone.infinitewall.data.versioncontrol.{ Repository, Branch, Read, Change }
import com.kindone.infinitewall.facades.CryptoJS
import upickle.default._

/**
 * Created by kindone on 2016. 7. 27..
 */
class VersionControl extends Synchronizer with History {

  val branch: Branch = new Branch(Branch.genHash)

  def append(change: Change): Unit = {

  }

  def rebase(changes: Seq[Change]): Unit = {

  }

  def getSeq(): Seq[Change] = {
    Seq()
  }
}
