package com.kindone.infinitewall.persistence

import scala.collection.mutable.HashMap
import upickle.default._
/**
 * Created by kindone on 2016. 2. 13..
 */
class SheetManager(localStorage: LocalStorage) {
  private val objectManager = new ObjectManager[Sheet](localStorage, "sheet")

  def get(id: Long): Sheet = {
    objectManager.getSheet(id).get
  }

  def create(x: Double, y: Double, width: Double, height: Double, text: String): Sheet = {
    val id = objectManager.nextId()
    val sheet = new Sheet(id, x, y, width, height, text)
    objectManager.save(id, sheet)
    println("sheet created")
    sheet
  }

  def delete(id: Long) = {
    objectManager.delete(id)
    println("sheet deleted")
  }

  def move(id: Long, x: Double, y: Double) = {
    val sheet = objectManager.getSheet(id).get
    val newSheet = new Sheet(id, x, y, sheet.width, sheet.height, sheet.text)
    objectManager.save(id, newSheet)
  }

  def resize(id: Long, width: Double, height: Double) = {
    val sheet = objectManager.getSheet(id).get
    val newSheet = new Sheet(sheet.id, sheet.x, sheet.y, width, height, sheet.text)
    objectManager.save(sheet.id, newSheet)
  }

  def setDimension(id: Long, x: Double, y: Double, width: Double, height: Double) = {
    val sheet = objectManager.getSheet(id).get
    val newSheet = new Sheet(sheet.id, x, y, width, height, sheet.text)
    objectManager.save(sheet.id, newSheet)
  }

  def setText(id: Long, text: String) = {
    val sheet = objectManager.getSheet(id).get
    val newSheet = new Sheet(sheet.id, sheet.x, sheet.y, sheet.width, sheet.height, text)
    objectManager.save(sheet.id, newSheet)
  }
}
