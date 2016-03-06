package com.kindone.infinitewall.persistence

import org.scalajs.dom
/**
 * Created by kindone on 2016. 2. 20..
 */
class LocalStorage {
  def getItem(key: String): Option[String] = {
    val result = dom.window.localStorage.getItem(key)
    if (result != null)
      Some(result)
    else
      None
  }

  def setItem(key: String, value: String): Unit = {
    dom.window.localStorage.setItem(key, value)
  }

  def removeItem(key: String): Unit = {
    dom.window.localStorage.removeItem(key)
  }

  def clear() = {
    dom.window.localStorage.clear()
  }
}
