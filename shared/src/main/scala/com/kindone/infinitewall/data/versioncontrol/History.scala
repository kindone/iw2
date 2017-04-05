package com.kindone.infinitewall.data.versioncontrol

import com.kindone.infinitewall.data.state.State

/**
 * Created by kindone on 2016. 12. 10..
 */

private case class ChangeStreak(states:Vector[JournaledState], changes:Vector[Change], stateId:Long)
{
  def applyChanges(changes:Seq[Change]):ChangeStreak = {
    changes.foldLeft(this) { (streak, change) =>
      val (newState, alteredChange) = streak.states.last.applyChange(change)
      ChangeStreak(streak.states :+ newState,
        streak.changes :+ alteredChange.copy(stateId = stateId),
        streak.stateId +1)
    }
  }
}

class History(baseState:State) {

  def append(change:Change) = {
    // update changes
    changes = changes :+ change
    // update snapshot
    val (newSnapshot, _) = states.last.applyChange(change)
    states = states :+ newSnapshot
  }

  // replace changes
  def rebase(newChanges:Seq[Change]):Unit = {

    val stateId = newChanges.head.stateId
    val (baseChanges, rebasedChanges) = splitChanges(stateId)
    val baseStates = getBaseStates(stateId)

    // apply new changes first
    val initialStreak = ChangeStreak(baseStates, baseChanges, stateId)
    val newStreak = initialStreak.applyChanges(newChanges)

    // apply old changes on top of it
    val newStreak2 = newStreak.applyChanges(rebasedChanges)

    states = newStreak2.states
    changes = newStreak2.changes
  }

  def stateId = {
    if(changes.isEmpty)
      states.last.stateId
    else
      changes.last.stateId + 1
  }

  def last:JournaledState = {
    states.last
  }



  private var states:Vector[JournaledState] = Vector(JournaledState.create(baseState))
  private var changes:Vector[Change] = Vector()

  /** private methods **/

  private def splitChanges(stateId:Long):(Vector[Change], Vector[Change]) = {
    val index = changes.indexWhere(_.stateId == stateId)
    changes.splitAt(index)
  }

  private def getBaseStates(stateId:Long):Vector[JournaledState] = {
    val index = changes.indexWhere(_.stateId == stateId)
    // s0-(c0)-s1-(c1)-s2- ... -
    // c1 -> [s0, s1]
    assert(index >= 0)
    states.take(index+1)
  }

  private def checkSanity() = {
    assert(changes.length + 1 == states.length)
    changes.foldLeft(0) { (i, change) =>
      assert(i == change.stateId)
      i+1
    }
  }

  private def checkSanity(changes:Seq[Change]) = {
    changes.foldLeft(0) { (i, change) =>
      assert(i == change.stateId)
      i+1
    }
  }

}
