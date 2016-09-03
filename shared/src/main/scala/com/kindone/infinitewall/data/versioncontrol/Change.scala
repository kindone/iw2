package com.kindone.infinitewall.data.versioncontrol

import com.kindone.infinitewall.data.State
import com.kindone.infinitewall.data.action.{ReadonlyAction, Action}
import upickle.default._
/**
 * Created by kindone on 2016. 7. 24..
 */
// json content to hash.
sealed trait VersionedAction {
  def parentHash:Option[String]
  def action:Action
}

trait Hasher
{
  def SHA1(input:String):String
}

case class Change(hash:String, /*changeType:String,*/ action:Action, parentHash:Option[String]) extends VersionedAction
case class Read(action:ReadonlyAction, parentHash:Option[String] = None) extends VersionedAction// none means latest

sealed abstract class Snapshot {
  def hash:String
  def applyChange(change:Change)(implicit hasher:Hasher):Snapshot
}

case class SingleSnapshot(hash:String, state:State) extends Snapshot {
  def applyChange(change:Change)(implicit hasher:Hasher):SingleSnapshot = {
    val newState = state.applyAction(change.action)
    val newHash = hasher.SHA1(write(newState))
    SingleSnapshot(newHash, newState)
  }
}

// single snapshots by Oid
case class GroupedSnapshot(hash:String, snapshots:Map[String, SingleSnapshot]) extends Snapshot
{
  def applyChange(change:Change)(implicit hasher:Hasher):GroupedSnapshot = {
    val newSnapshots =
      for((oid, snapshot) <-snapshots)
        yield
      {
        val newState = snapshot.state.applyAction(change.action)
        val newHash = hasher.SHA1(write(newState))
        (oid, SingleSnapshot(newHash, newState))
      }
    val newHash = hasher.SHA1(write(newSnapshots))
    GroupedSnapshot(newHash, newSnapshots)
  }
}

case class TextualChange(content:String, pos:Int, length:Int)