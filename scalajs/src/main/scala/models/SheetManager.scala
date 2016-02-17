package com.kindone.infinitewall.models

import scala.collection.mutable.HashMap
/**
 * Created by kindone on 2016. 2. 13..
 */
class SheetManager {

  private var maxId: Long = 0
  private var sheets: HashMap[Long, Sheet] = HashMap()

  def nextId() = {
    maxId += 1
    maxId
  }

  def create(x: Double, y: Double, width: Double, height: Double, text: String): Sheet = {
    val id = nextId()
    val sheet = new Sheet(id, x, y, width, height, text)
    sheets += (id -> sheet)
    sheet
  }

  def delete(id: Long) = {
    sheets -= id
  }

  def move(id: Long, x: Double, y: Double) = {
    val sheet = sheets.get(id).get
    val newSheet = new Sheet(sheet.id, x, y, sheet.width, sheet.height, sheet.text)
    sheets += (id -> newSheet)
  }

  def resize(id: Long, width: Double, height: Double) = {
    val sheet = sheets.get(id).get
    val newSheet = new Sheet(sheet.id, sheet.x, sheet.y, width, height, sheet.text)
    sheets += (id -> newSheet)
  }

  def setText(id: Long, text: String) = {
    val sheet = sheets.get(id).get
    val newSheet = new Sheet(sheet.id, sheet.x, sheet.y, sheet.width, sheet.height, text)
    sheets += (id -> newSheet)
  }
}
