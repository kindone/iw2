package com.kindone.infinitewall.persistence.wsstorage

import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.versioncontrol.Branch
import com.kindone.infinitewall.data.{ Wall, Sheet }
import com.kindone.infinitewall.event.EventListener
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent
import com.kindone.infinitewall.persistence.api.{ SimplePersistence, Persistence }
import com.kindone.infinitewall.persistence.wsstorage.socket.{ MailboxSocket, MailboxWebSocket }

import scala.concurrent.Future

/**
 * Created by kindone on 2016. 4. 17..
 */
class WebSocketPersistence(socket: MailboxWebSocket) extends Persistence {
  private val branch: Branch = Branch.create()
  private val messageProcessor = new MessageProcessor(branch, socket)

  def getSheet(sheetId: Long)(implicit stateId: Long): Future[Sheet] = {
    messageProcessor.send[Sheet](GetSheetAction(sheetId))
  }

  def moveSheet(sheetId: Long, x: Double, y: Double)(implicit stateId: Long): Future[Boolean] = {
    messageProcessor.send[Boolean](MoveSheetAction(sheetId, x, y))
  }

  def resizeSheet(sheetId: Long, width: Double, height: Double)(implicit stateId: Long): Future[Boolean] = {
    messageProcessor.send[Boolean](ResizeSheetAction(sheetId, width, height))
  }

  def setSheetDimension(sheetId: Long, x: Double, y: Double, width: Double, height: Double)(implicit stateId: Long): Future[Boolean] = {
    messageProcessor.send[Boolean](ChangeSheetDimensionAction(sheetId, x, y, width, height))
  }

  def setSheetText(sheetId: Long, text: String)(implicit stateId: Long): Future[Boolean] = {
    messageProcessor.send[Boolean](ChangeSheetContentAction(sheetId, text))
  }

  override def subscribeSheet(sheetId: Long)(implicit stateId: Long): Future[Boolean] = {
    messageProcessor.send[Boolean](SubscribeSheetEventAction(sheetId))
  }

  override def addOnSheetUpdateEventHandler(sheetId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    messageProcessor.addOnSheetNotificationListener(sheetId, handler)
  }

  def createWall(title: String, x: Double = 0, y: Double = 0, scale: Double = 1.0): Future[Wall] = {
    messageProcessor.send[Wall](CreateWallAction(title, x, y, scale))
  }

  def deleteWall(wallId: Long): Future[Boolean] = {
    messageProcessor.send[Boolean](DeleteWallAction(wallId))
  }

  def getWall(wallId: Long): Future[Option[Wall]] = {
    messageProcessor.send[Option[Wall]](GetWallAction(wallId))
  }

  def getWalls(): Future[Seq[Wall]] = {
    messageProcessor.send[Seq[Wall]](ListWallAction())
  }

  def panWall(wallId: Long, x: Double, y: Double)(implicit stateId: Long): Future[Boolean] = {
    messageProcessor.send[Boolean](ChangePanAction(wallId, x, y))
  }

  def zoomWall(wallId: Long, scale: Double)(implicit stateId: Long): Future[Boolean] = {
    messageProcessor.send[Boolean](ChangeZoomAction(wallId, scale))
  }

  def setWallView(wallId: Long, x: Double, y: Double, scale: Double)(implicit stateId: Long): Future[Boolean] = {
    messageProcessor.send[Boolean](ChangeViewAction(wallId, x, y, scale))
  }

  def setWallTitle(wallId: Long, title: String)(implicit stateId: Long): Future[Boolean] = {
    messageProcessor.send[Boolean](ChangeTitleAction(wallId, title))
  }

  def getSheetsInWall(wallId: Long)(implicit stateId: Long): Future[Set[Long]] = {
    messageProcessor.send[Set[Long]](ListSheetAction(wallId))
  }

  def createSheetInWall(wallId: Long, x: Double, y: Double, width: Double, height: Double, text: String)(implicit stateId: Long): Future[Sheet] = {
    messageProcessor.send[Sheet](CreateSheetAction(wallId, Sheet(0, 0, x, y, width, height, text)))
  }

  def deleteSheetInWall(wallId: Long, sheetId: Long)(implicit stateId: Long): Future[Boolean] = {
    messageProcessor.send[Boolean](DeleteSheetAction(wallId, sheetId))
  }

  override def subscribeWall(wallId: Long)(implicit stateId: Long): Future[Boolean] = {
    messageProcessor.send[Boolean](SubscribeWallEventAction(wallId))
  }

  override def addOnWallUpdateEventHandler(wallId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    messageProcessor.addOnWallNotificationListener(wallId, handler)
  }

  def clear(): Unit = {

  }
}
