package com.kindone.infinitewall.persistence

import com.kindone.infinitewall.persistence.Sheet
import upickle.default._

/**
 * Created by kindone on 2016. 2. 20..
 */
class ObjectManager[T](localStorage: LocalStorage, name: String) {

  def save(id: Long, obj: Sheet) = {
    localStorage.setItem(objectKey(id), write(obj))
    register(id)
  }

  def save(id: Long, obj: Wall) = {
    localStorage.setItem(objectKey(id), write(obj))
    register(id)
  }

  def getSheet(id: Long): Option[Sheet] = {
    localStorage.getItem(objectKey(id)).map(read[Sheet](_))
  }

  def getWall(id: Long): Option[Wall] = {
    localStorage.getItem(objectKey(id)).map(read[Wall](_))
  }

  def getWalls(): Seq[Wall] = {
    val ids = localStorage.getItem(objectSetKey).map(read[Seq[Long]](_))
    ids.getOrElse(Seq()).map { id =>
      getWall(id).get
    }
  }

  def delete(id: Long) = {
    localStorage.removeItem(objectKey(id))
    unregister(id)
  }

  private val prefix = name + "_"

  private def objectKey(id: Long) = prefix + "object_" + id
  private val maxIdKey = prefix + "maxId"
  private val objectSetKey = prefix + "objectSet"

  private def maxId: Long = {
    val value = localStorage.getItem(maxIdKey)
    if (value.isEmpty) {
      localStorage.setItem(maxIdKey, "0")
      0
    } else
      value.get.toLong
  }

  private def maxId_=(value: Long): Unit = {
    localStorage.setItem(prefix + "maxId", value.toString)
  }

  def nextId() = {
    maxId = maxId + 1
    maxId
  }

  private var objects: Set[Long] = localStorage.getItem(objectSetKey).map(read[Set[Long]]).getOrElse(Set())

  private def register(id: Long) = {
    objects = objects + id
    localStorage.setItem(objectSetKey, write(objects))
  }

  private def unregister(id: Long) = {
    objects = objects - id
    localStorage.setItem(objectSetKey, write(objects))
  }
}
