package com.kindone.infinitewall.persistence

import com.kindone.infinitewall.persistence.Sheet
import upickle.default._

/**
 * Created by kindone on 2016. 2. 20..
 */
class ObjectManager[T](localStorageManager: LocalStorageManager, name: String) {

  def save(id: Long, obj: Sheet) = {
    localStorageManager.setItem(objectKey(id), write(obj))
    register(id)
  }

  def save(id: Long, obj: Wall) = {
    localStorageManager.setItem(objectKey(id), write(obj))
    register(id)
  }

  def getSheet(id: Long): Option[Sheet] = {
    localStorageManager.getItem(objectKey(id)).map(read[Sheet](_))
  }

  def getWall(id: Long): Option[Wall] = {
    localStorageManager.getItem(objectKey(id)).map(read[Wall](_))
  }

  def delete(id: Long) = {
    localStorageManager.removeItem(objectKey(id))
    unregister(id)
  }

  private val prefix = name + "_"

  private def objectKey(id: Long) = prefix + "object_" + id
  private val maxIdKey = prefix + "maxId"
  private val objectSetKey = prefix + "objectSet"

  private def maxId: Long = {
    val value = localStorageManager.getItem(maxIdKey)
    if (value.isEmpty) {
      localStorageManager.setItem(maxIdKey, "0")
      0
    } else
      value.get.toLong
  }

  private def maxId_=(value: Long): Unit = {
    localStorageManager.setItem(prefix + "maxId", value.toString)
  }

  def nextId() = {
    maxId = maxId + 1
    maxId
  }

  private var objects: Set[Long] = localStorageManager.getItem(objectSetKey).map(read[Set[Long]]).getOrElse(Set())

  private def register(id: Long) = {
    objects = objects + id
    localStorageManager.setItem(objectSetKey, write(objects))
  }

  private def unregister(id: Long) = {
    objects = objects - id
    localStorageManager.setItem(objectSetKey, write(objects))
  }
}
