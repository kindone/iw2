package com.kindone.infinitewall.data.versioncontrol

import com.kindone.infinitewall.data.{Wall, Sheet}
import com.kindone.infinitewall.data.action.{ChangeSheetContentAction, Action}
import com.kindone.infinitewall.data.versioncontrol.util.{TextOperation, StringWithState}

/**
 * Created by kindone on 2016. 7. 24..
 */
class Repository {

  private var changes:Map[String, Change] = Map() // index: hash -> change
  private var parentHashMap:Map[String, String] = Map() // index: parentHash -> hash
  private var head:Option[Change] = None // TODO
  private var snapshots:Map[String, Snapshot] = Map() // initial + optimization

  private val remotes:List[Repository] = List() // TODO

  def findChange(hash:String):Option[Change] = {
    changes.get(hash)
  }

  def initialize(snapshot:Snapshot) = {
    snapshots = Map(snapshot.hash -> snapshot)
  }

  // save change in changes and return rebased change if altered
  def saveChange(change:Change):Change = {
    // valid only when there is no key in the change
    if(changes.get(change.hash).isEmpty) {
      // rebase if parent exists
      if(change.parentHash.isDefined && changes.get(change.parentHash.get).isDefined) {
        changes = changes + (change.hash -> change)
        rebaseIfNeeded(change)
      }
      // save initial change if no change is present
      else if(change.parentHash.isEmpty && changes.isEmpty) {
        changes = changes + ("" -> change)
        change
      }
      else
        throw new IllegalArgumentException("parentHash is invalid: " + change.parentHash.toString)
    }
    else
      throw new IllegalArgumentException("hash for change already exists: " + change.hash)
  }


  // find upstream for state
  private def getSnapshot(hash:String):Snapshot = {

    if(snapshots.get(hash).isDefined)
    {
      snapshots.get(hash).get
    }
    else
    {
      var change = changes.get(hash).get
      var changesToApply = change +: List()

      while(snapshots.get(change.parentHash.get).isEmpty)
      {
        change = changes.get(change.parentHash.get).get
        changesToApply = change +: changesToApply
      }
      val baseSnapshot = snapshots.get(change.parentHash.get).get
      var snapshot = baseSnapshot
      for(change <- changesToApply)
      {
        snapshot = snapshot.applyChange(change)
      }
      snapshot
    }
  }

  private def rebaseIfNeeded(change:Change):Change = {
    // if there is no collision(shares same parent), just go
    val collision = parentHashMap.get(change.parentHash.get)
    if(collision.isEmpty) {
      parentHashMap = parentHashMap + (change.parentHash.get -> change.hash)
      change
    }
    // if there is a collision, rebase
    // rebase: replace with change
    else {
      //collision
      // new change generated
      change
    }
  }

  // first apply target by inserting, then rebase the rest
  private def rebaseTextualChange(base:String, changesToRebase:List[TextualChange], target:TextualChange) = {
    val string = new StringWithState(base)
    val firstOp = TextOperation(target.pos, target.length, target.content)
    val newChanges =
      for(change <- changesToRebase)
      yield
    {
      val secondOp = TextOperation(change.pos, change.length, change.content)
      string.apply(firstOp, 0)
      val transformedSecondOp = string.apply(secondOp, 1)
      TextualChange(transformedSecondOp.content, transformedSecondOp.from, transformedSecondOp.length)
    }
    (string.text, newChanges)
  }
}
