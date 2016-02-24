package com.kindone.infinitewall.persistence

import upickle.default._

/**
 * Created by kindone on 2016. 2. 20..
 */
class RelationManager(localStorageManager: LocalStorageManager, name: String) {
  private val prefix = name + "_"

  private def objectSetKey(subjectId: Long) = prefix + "objectSet_" + subjectId

  private def objects(subjectId: Long): Set[Long] = localStorageManager.getItem(objectSetKey(subjectId)).map(read[Set[Long]]).getOrElse(Set())

  def create(subjectId: Long, objectId: Long) = {
    register(subjectId, objectId)
  }

  def get(subjectId: Long): Set[Long] = {
    objects(subjectId)
  }

  def delete(subjectId: Long, objectId: Long) = {
    unregister(subjectId, objectId)
  }

  private def register(subjectId: Long, objectId: Long) = {
    localStorageManager.setItem(objectSetKey(subjectId), write(objects(subjectId) + objectId))
  }

  private def unregister(subjectId: Long, objectId: Long) = {
    localStorageManager.setItem(objectSetKey(subjectId), write(objects(subjectId) - objectId))
  }

}
