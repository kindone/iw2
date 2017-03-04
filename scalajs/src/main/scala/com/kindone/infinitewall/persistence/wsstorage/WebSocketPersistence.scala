package com.kindone.infinitewall.persistence.wsstorage

import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.versioncontrol.Branch
import com.kindone.infinitewall.data.{ Wall, Sheet }
import com.kindone.infinitewall.events.EventListener
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent
import com.kindone.infinitewall.persistence.api.{ SimplePersistence, Persistence }
import com.kindone.infinitewall.persistence.wsstorage.sockets.{ MailboxSocket, MailboxWebSocket }

import scala.concurrent.Future

/**
 * Created by kindone on 2016. 4. 17..
 */
class WebSocketPersistence(socket: MailboxWebSocket) extends Persistence {
  private val branch: Branch = Branch.create()
  private val processor = new MessageProcessor(branch, socket)

  def getSheet(sheetId: Long): Future[Sheet] = {
    processor.send[Sheet](GetSheetAction(sheetId))
  }

  def moveSheet(sheetId: Long, x: Double, y: Double): Future[Boolean] = {
    processor.send[Boolean](MoveSheetAction(sheetId, x, y))
  }

  def resizeSheet(sheetId: Long, width: Double, height: Double): Future[Boolean] = {
    processor.send[Boolean](ResizeSheetAction(sheetId, width, height))
  }

  def setSheetDimension(sheetId: Long, x: Double, y: Double, width: Double, height: Double): Future[Boolean] = {
    processor.send[Boolean](ChangeSheetDimensionAction(sheetId, x, y, width, height))
  }

  def setSheetText(sheetId: Long, text: String): Future[Boolean] = {
    processor.send[Boolean](ChangeSheetContentAction(sheetId, text))
  }

  override def subscribeSheet(sheetId: Long): Future[Boolean] = {
    processor.send[Boolean](SubscribeSheetEventAction(sheetId))
  }

  override def addOnSheetUpdateEventHandler(sheetId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    processor.addOnSheetNotificationListener(sheetId, handler)
  }

  def createWall(title: String, x: Double = 0, y: Double = 0, scale: Double = 1.0): Future[Wall] = {
    processor.send[Wall](CreateWallAction(title, x, y, scale))
  }

  def deleteWall(wallId: Long): Future[Boolean] = {
    processor.send[Boolean](DeleteWallAction(wallId))
  }

  def getWall(wallId: Long): Future[Option[Wall]] = {
    processor.send[Option[Wall]](GetWallAction(wallId))
  }

  def getWalls(): Future[Seq[Wall]] = {
    processor.send[Seq[Wall]](ListWallAction())
  }

  def panWall(wallId: Long, x: Double, y: Double): Future[Boolean] = {
    processor.send[Boolean](ChangePanAction(wallId, x, y))
  }

  def zoomWall(wallId: Long, scale: Double): Future[Boolean] = {
    processor.send[Boolean](ChangeZoomAction(wallId, scale))
  }

  def setWallView(wallId: Long, x: Double, y: Double, scale: Double): Future[Boolean] = {
    processor.send[Boolean](ChangeViewAction(wallId, x, y, scale))
  }

  def setWallTitle(wallId: Long, title: String): Future[Boolean] = {
    processor.send[Boolean](ChangeTitleAction(wallId, title))
  }

  def getSheetsInWall(wallId: Long): Future[Set[Long]] = {
    processor.send[Set[Long]](ListSheetAction(wallId))
  }

  def createSheetInWall(wallId: Long, x: Double, y: Double, width: Double, height: Double, text: String): Future[Sheet] = {
    processor.send[Sheet](CreateSheetAction(wallId, Sheet(0, 0, x, y, width, height, text)))
  }

  def deleteSheetInWall(wallId: Long, sheetId: Long): Future[Boolean] = {
    processor.send[Boolean](DeleteSheetAction(wallId, sheetId))
  }

  override def subscribeWall(wallId: Long): Future[Boolean] = {
    processor.send[Boolean](SubscribeWallEventAction(wallId))
  }

  override def addOnWallUpdateEventHandler(wallId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    processor.addOnWallNotificationListener(wallId, handler)
  }

  def clear(): Unit = {

  }
}
