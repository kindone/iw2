package com.kindone.infinitewall.persistence.wsstorage

import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.{ Wall, Sheet }
import com.kindone.infinitewall.events.EventListener
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent
import com.kindone.infinitewall.persistence.api.{ SimplePersistence, Persistence }
import com.kindone.infinitewall.persistence.wsstorage.sockets.MailboxSocket
import com.kindone.infinitewall.versioncontrol.{ SyncMerger, SyncBuffer, VersionControl }

import scala.concurrent.Future

/**
 * Created by kindone on 2016. 4. 17..
 */
class WebSocketPersistence(baseUrl: String) extends Persistence {
  private val versionControl = new VersionControl
  private val socket = new MailboxSocket(baseUrl)

  def getSheet(sheetId: Long): Future[Sheet] = {
    socket.send[Sheet](GetSheetAction(sheetId))
  }

  def moveSheet(sheetId: Long, x: Double, y: Double): Future[Boolean] = {
    socket.send[Boolean](MoveSheetAction(sheetId, x, y))
  }

  def resizeSheet(sheetId: Long, width: Double, height: Double): Future[Boolean] = {
    socket.send[Boolean](ResizeSheetAction(sheetId, width, height))
  }

  def setSheetDimension(sheetId: Long, x: Double, y: Double, width: Double, height: Double): Future[Boolean] = {
    socket.send[Boolean](ChangeSheetDimensionAction(sheetId, x, y, width, height))
  }

  def setSheetText(sheetId: Long, text: String): Future[Boolean] = {
    socket.send[Boolean](ChangeSheetContentAction(sheetId, text))
  }

  override def subscribeSheet(sheetId: Long): Future[Boolean] = {
    socket.subscribeSheet(sheetId)
  }

  override def addOnSheetUpdateEventHandler(sheetId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    socket.addOnSheetNotificationListener(sheetId, handler)
  }

  def createWall(title: String, x: Double = 0, y: Double = 0, scale: Double = 1.0): Future[Wall] = {
    socket.send[Wall](CreateWallAction(title, x, y, scale))
  }

  def deleteWall(wallId: Long): Future[Boolean] = {
    socket.send[Boolean](DeleteWallAction(wallId))
  }

  def getWall(wallId: Long): Future[Option[Wall]] = {
    socket.send[Option[Wall]](GetWallAction(wallId))
  }

  def getWalls(): Future[Seq[Wall]] = {
    socket.send[Seq[Wall]](ListWallAction())
  }

  def panWall(wallId: Long, x: Double, y: Double): Future[Boolean] = {
    socket.send[Boolean](ChangePanAction(wallId, x, y))
  }

  def zoomWall(wallId: Long, scale: Double): Future[Boolean] = {
    socket.send[Boolean](ChangeZoomAction(wallId, scale))
  }

  def setWallView(wallId: Long, x: Double, y: Double, scale: Double): Future[Boolean] = {
    socket.send[Boolean](ChangeViewAction(wallId, x, y, scale))
  }

  def setWallTitle(wallId: Long, title: String): Future[Boolean] = {
    socket.send[Boolean](ChangeTitleAction(wallId, title))
  }

  def getSheetsInWall(wallId: Long): Future[Set[Long]] = {
    socket.send[Set[Long]](ListSheetAction(wallId))
  }

  def createSheetInWall(wallId: Long, x: Double, y: Double, width: Double, height: Double, text: String): Future[Sheet] = {
    socket.send[Sheet](CreateSheetAction(wallId, Sheet(0, 0, x, y, width, height, text)))
  }

  def deleteSheetInWall(wallId: Long, sheetId: Long): Future[Boolean] = {
    socket.send[Boolean](DeleteSheetAction(wallId, sheetId))
  }

  override def subscribeWall(wallId: Long): Future[Boolean] = {
    socket.subscribeWall(sheetId)
  }

  override def addOnWallUpdateEventHandler(wallId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    socket.addOnWallNotificationListener(wallId, handler)
  }

  def clear(): Unit = {

  }
}
