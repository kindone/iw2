package com.kindone.infinitewall.persistence.localstorage

import com.kindone.infinitewall.data.Sheet
import com.kindone.infinitewall.persistence.api.{ SheetManager => SheetManagerAPI }

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

/**
 * Created by kindone on 2016. 2. 13..
 */
class SheetManager(localStorage: LocalStorage) extends SheetManagerAPI {
  private val objectManager = new ObjectManager[Sheet](localStorage, "sheet")

  def get(id: Long): Future[Sheet] = Future {
    objectManager.getSheet(id).get
  }

  def create(x: Double, y: Double, width: Double, height: Double, text: String): Future[Sheet] = Future {
    val id = objectManager.nextId()
    val sheet = new Sheet(id, 0, x, y, width, height, text)
    objectManager.save(id, sheet)
    println("sheet created")
    sheet
  }

  def delete(id: Long) = Future {
    objectManager.delete(id)
    println("sheet deleted")
    true
  }

  def move(id: Long, x: Double, y: Double) = Future {
    val sheet = objectManager.getSheet(id).get
    val newSheet = new Sheet(id, 0, x, y, sheet.width, sheet.height, sheet.text)
    objectManager.save(id, newSheet)
    true
  }

  def resize(id: Long, width: Double, height: Double) = Future {
    val sheet = objectManager.getSheet(id).get
    val newSheet = new Sheet(sheet.id, 0, sheet.x, sheet.y, width, height, sheet.text)
    objectManager.save(sheet.id, newSheet)
    true
  }

  def setDimension(id: Long, x: Double, y: Double, width: Double, height: Double) = Future {
    val sheet = objectManager.getSheet(id).get
    val newSheet = new Sheet(sheet.id, 0, x, y, width, height, sheet.text)
    objectManager.save(sheet.id, newSheet)
    true
  }

  def setText(id: Long, text: String) = Future {
    val sheet = objectManager.getSheet(id).get
    val newSheet = new Sheet(sheet.id, 0, sheet.x, sheet.y, sheet.width, sheet.height, text)
    objectManager.save(sheet.id, newSheet)
    true
  }
}
