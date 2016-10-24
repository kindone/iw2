package com.kindone.infinitewall.data.versioncontrol

import com.kindone.infinitewall.data.State

/**
 * Created by kindone on 2016. 10. 2..
 */
class Repository(baseState:State) {
  var states:Vector[StateWithHistory] = Vector(StateWithHistory.create(baseState))
  var changes:Vector[Change] = Vector()

  def append(change:Change):Unit = {
    checkSanity(change)

    changes = changes :+ change
    val (newSnapshot, _) = states.last.applyChange(change)
    states = states :+ newSnapshot

    checkSanity()
  }

  // replace changes
  def rebase(newChanges:Seq[Change]) = {
    checkSanity(newChanges)

    val targetChanges = getChanges(newChanges.head)
    val baseChanges = getBaseChanges(newChanges.head)
    val baseStates = getBaseStates(newChanges.head)
    val baseLogId = newChanges.head.baseLogId

    // apply new changes first
    val newStream1 = newChanges.foldLeft((baseStates, baseChanges, baseLogId)) { (stream, change) =>
      val (states, changes, baseLogId) = stream
      val (newState, alteredChange) = states.last.applyChange(change)

      (states :+ newState, changes :+ alteredChange.copy(baseLogId = baseLogId), baseLogId +1)
    }

    // apply old changes on top of it
    val newStream2 = targetChanges.foldLeft(newStream1) { (stream, change) =>
      val (states, changes, baseLogId) = stream
      val (newState, alteredChange) = states.last.applyChange(change)

      (states :+ newState, changes :+ alteredChange.copy(baseLogId = baseLogId), baseLogId +1)
    }

    states = newStream2._1
    //changes = baseChanges ++ newChanges ++ targetChanges
    changes = newStream2._2

    checkSanity()
  }

  def getChanges(baseChange:Change) = {
    changes.dropWhile(_.baseLogId != baseChange.baseLogId)
  }

  def getLatestState():StateWithHistory = {
    states.last
  }

  def baseLogId = {
    if(changes.isEmpty)
      0L
    else
      changes.last.baseLogId + 1
  }

  def getBaseState(baseChange:Change):StateWithHistory = {
    val index = changes.indexWhere(_.baseLogId == baseChange.baseLogId)
    assert(index >= 0)
    states(index)
  }

  def getBaseChanges(baseChange:Change):Vector[Change] = {
    changes.takeWhile(_.baseLogId != baseChange.baseLogId)
  }

  def getBaseStates(baseChange:Change):Vector[StateWithHistory] = {
    val index = changes.indexWhere(_.baseLogId == baseChange.baseLogId)
    // s0-(c0)-s1-(c1)-s2- ... -
    // c1 -> [s0, s1]
    assert(index >= 0)
    states.take(index+1)
  }

  private def checkSanity() = {
    assert(changes.length + 1 == states.length)
    changes.foldLeft(0) { (i, change) =>
      assert(i == change.baseLogId)
      i+1
    }
  }

  private def checkSanity(changes:Seq[Change]) = {
    changes.foldLeft(0) { (i, change) =>
      assert(i == change.baseLogId)
      i+1
    }
  }

  private def checkSanity(change:Change) = {
    assert(baseLogId == change.baseLogId)
  }

}
