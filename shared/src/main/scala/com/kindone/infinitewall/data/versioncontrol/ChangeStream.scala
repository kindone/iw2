package com.kindone.infinitewall.data.versioncontrol

import com.kindone.infinitewall.data.state.State

/**
 * Created by kindone on 2016. 12. 10..
 */

case class PartialChangeStream(states:Vector[StateWithHistory], changes:Vector[Change], baseLogId:Long)
{
  def applyChanges(changes:Seq[Change]):PartialChangeStream = {
    changes.foldLeft(this) { (stream, change) =>
      val (newState, alteredChange) = stream.states.last.applyChange(change)
      PartialChangeStream(stream.states :+ newState,
        stream.changes :+ alteredChange.copy(baseLogId = baseLogId),
        stream.baseLogId +1)
    }
  }
}

class ChangeStream(baseState:State) {
  private var states:Vector[StateWithHistory] = Vector(StateWithHistory.create(baseState))
  private var changes:Vector[Change] = Vector()

  def append(change:Change) = {
    changes = changes :+ change
    val (newSnapshot, _) = states.last.applyChange(change)
    states = states :+ newSnapshot
  }

  // replace changes
  def rebase(newChanges:Seq[Change]):Unit = {

    val baseLogId = newChanges.head.baseLogId
    val (baseChanges, rebasedChanges) = splitChanges(baseLogId)
    val baseStates = getBaseStates(baseLogId)

    // apply new changes first
    val initialStream = PartialChangeStream(baseStates, baseChanges, baseLogId)
    val newStream1 = initialStream.applyChanges(newChanges)

    // apply old changes on top of it
    val newStream2 = newStream1.applyChanges(rebasedChanges)

    states = newStream2.states
    changes = newStream2.changes
  }

  def baseLogId = {
    if(changes.isEmpty)
      states.last.stateId
    else
      changes.last.baseLogId + 1
  }

  def getLatestState():StateWithHistory = {
    states.last
  }

  private def splitChanges(baseLogId:Long):(Vector[Change], Vector[Change]) = {
    val index = changes.indexWhere(_.baseLogId == baseLogId)
    changes.splitAt(index)
  }

  private def getBaseStates(baseLogId:Long):Vector[StateWithHistory] = {
    val index = changes.indexWhere(_.baseLogId == baseLogId)
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

}
