package com.kindone.infinitewall.models

/**
 * Created by kindone on 2016. 2. 13..
 */
class WallManager {

  def pan(id: Long, x: Double, y: Double) = {}
  def zoom(id: Long, scale: Double) = {}

  def appendSheet(id: Long, sheetId: Long) = {}
  def removeSheet(id: Long, sheetId: Long) = {}
}
