package com.kindone.infinitewall.persistence.wsstorage

import com.kindone.infinitewall.data.Sheet
import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.versioncontrol.{ Read, Change }
import com.kindone.infinitewall.events.EventListener
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent
import com.kindone.infinitewall.persistence.api.{ SheetManager => SheetManagerAPI }
import com.kindone.infinitewall.versioncontrol.VersionControl

import scala.concurrent.Future

/**
 * Created by kindone on 2016. 4. 17..
 */
class SheetManager(socket: Socket) extends SheetManagerAPI {
  val vc = new VersionControl

  def genChange(action: WriteAction): Change = {
    // val head = repository.head.match { x => Some(_) }

    vc.createChange(action, 0) // TODO: fill logId
    // repository.saveChange()
  }

  def genRead(action: ReadonlyAction): Read = {
    vc.createRead(action, 0) // TODO: fill logId
  }

  def get(sheetId: Long): Future[Sheet] = {
    socket.send[Sheet](genRead(GetSheetAction(sheetId)))
  }

  def move(sheetId: Long, x: Double, y: Double): Future[Boolean] = {
    socket.send[Boolean](genChange(MoveSheetAction(sheetId, x, y)))
  }

  def resize(sheetId: Long, width: Double, height: Double): Future[Boolean] = {
    socket.send[Boolean](genChange(ResizeSheetAction(sheetId, width, height)))
  }

  def setDimension(sheetId: Long, x: Double, y: Double, width: Double, height: Double): Future[Boolean] = {
    socket.send[Boolean](genChange(ChangeSheetDimensionAction(sheetId, x, y, width, height)))
  }

  def setText(sheetId: Long, text: String): Future[Boolean] = {
    socket.send[Boolean](genChange(ChangeSheetContentAction(sheetId, text)))
  }

  override def subscribe(sheetId: Long): Future[Boolean] = {
    socket.send[Boolean](genRead(SubscribeSheetEventAction(sheetId)))
  }

  override def addOnUpdateEventHandler(sheetId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    socket.addOnSheetNotificationListener(sheetId, handler)
  }
}
