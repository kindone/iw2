package com.kindone.infinitewall.data.versioncontrol

import com.kindone.infinitewall.data.action.{ReadonlyAction, Action}
import upickle.default._
import com.kindone.util.Hasher
/**
 * Created by kindone on 2016. 7. 24..
 */
// json content to hash.


trait VersionedAction {
  def action:Action
  def baseLogId:Long
}

case class Change(action:Action, baseLogId:Long, branch:Branch) extends VersionedAction
case class Read(action:ReadonlyAction, baseLogId:Long) extends VersionedAction// none means latest

