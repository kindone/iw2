package com.kindone.infinitewall.persistence.wsstorage

import com.kindone.infinitewall.data.Sheet
import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.events.EventListener
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent
import com.kindone.infinitewall.persistence.api.{ SheetManager => SheetManagerAPI }

import scala.concurrent.Future

/**
 * Created by kindone on 2016. 4. 17..
 */
class SheetManager(socket: Socket) extends SheetManagerAPI {
  def get(sheetId: Long): Future[Sheet] = {
    socket.send[Sheet](GetSheetAction(sheetId))
  }

  def move(sheetId: Long, x: Double, y: Double): Future[Boolean] = {
    socket.send[Boolean](MoveSheetAction(sheetId, x, y))
  }

  def resize(sheetId: Long, width: Double, height: Double): Future[Boolean] = {
    socket.send[Boolean](ResizeSheetAction(sheetId, width, height))
  }

  def setDimension(sheetId: Long, x: Double, y: Double, width: Double, height: Double): Future[Boolean] = {
    socket.send[Boolean](ChangeSheetDimensionAction(sheetId, x, y, width, height))
  }

  def setText(sheetId: Long, text: String): Future[Boolean] = {
    socket.send[Boolean](ChangeSheetContentAction(sheetId, text))
  }

  override def subscribe(sheetId: Long): Future[Boolean] = {
    socket.send[Boolean](SubscribeSheetEventAction(sheetId))
  }

  override def addOnUpdateEventHandler(sheetId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    socket.addOnSheetNotificationListener(sheetId, handler)
  }
}
