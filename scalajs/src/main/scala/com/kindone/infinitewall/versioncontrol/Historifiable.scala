package com.kindone.infinitewall.versioncontrol

import com.kindone.infinitewall.data.action.Action

/**
 * Created by kindone on 2016. 7. 17..
 */
trait Historifiable {
  // server-consolidated actions
  private var history: List[(Long, Action)] = List()
  // client-made unconsolidate actions
  private var queuedActions: List[Action] = List()
  private var minStateId = 0L
  private var maxStateId = 0L

  // add to client-side queue
  def enqueue(action: Action) = {
    queuedActions = queuedActions :+ action
    maxStateId += 1
  }

  def dequeue() = {
    queuedActions = queuedActions.drop(1)
    minStateId += 1
  }

  // consolidate server-side action
  def append(stateId: Long, action: Action) = {
    history = history :+ (stateId, action)
  }
}
