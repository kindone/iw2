package com.kindone.infinitewall.persistence.api

import com.kindone.infinitewall.data.state.{ Wall, Sheet }
import com.kindone.infinitewall.event.EventListener
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent

import scala.concurrent.Future

/**
 * Created by kindone on 2016. 10. 29..
 */
trait SimplePersistence extends Persistence {
  protected val sheetManager: SheetManager
  protected val wallManager: WallManager

  /* sheet */
  def getSheet(sheetId: Long)(implicit stateId: Long): Future[Sheet] =
    sheetManager.get(sheetId)

  def moveSheet(sheetId: Long, x: Double, y: Double)(implicit stateId: Long): Future[Boolean] =
    sheetManager.move(sheetId, x, y)

  def resizeSheet(sheetId: Long, width: Double, height: Double)(implicit stateId: Long): Future[Boolean] =
    sheetManager.resize(sheetId, width, height)

  def setSheetDimension(sheetId: Long, x: Double, y: Double, width: Double, height: Double)(implicit stateId: Long): Future[Boolean] =
    sheetManager.setDimension(sheetId, x, y, width, height)

  def setSheetText(sheetId: Long, text: String)(implicit stateId: Long): Future[Boolean] =
    sheetManager.setText(sheetId, text)

  def subscribeSheet(sheetId: Long)(implicit stateId: Long): Future[Boolean] =
    sheetManager.subscribe(sheetId)

  def addOnSheetUpdateEventHandler(sheetId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit =
    sheetManager.addOnUpdateEventHandler(sheetId, handler)

  /* wall */
  def createWall(title: String, x: Double = 0, y: Double = 0, scale: Double = 1.0): Future[Wall] =
    wallManager.create(title, x, y, scale)

  def deleteWall(wallId: Long): Future[Boolean] = wallManager.delete(wallId)

  def getWall(wallId: Long): Future[Option[Wall]] = wallManager.get(wallId)

  def getWalls(): Future[Seq[Wall]] = wallManager.getWalls()

  def panWall(wallId: Long, x: Double, y: Double)(implicit stateId: Long): Future[Boolean] =
    wallManager.pan(wallId, x, y)

  def zoomWall(wallId: Long, scale: Double)(implicit stateId: Long): Future[Boolean] =
    wallManager.zoom(wallId, scale)

  def setWallView(wallId: Long, x: Double, y: Double, scale: Double)(implicit stateId: Long): Future[Boolean] =
    wallManager.setView(wallId, x, y, scale)

  def setWallTitle(wallId: Long, title: String)(implicit stateId: Long): Future[Boolean] =
    wallManager.setTitle(wallId, title)

  def getSheetsInWall(wallId: Long)(implicit stateId: Long): Future[Set[Long]] =
    wallManager.getSheets(wallId)

  def createSheetInWall(wallId: Long, x: Double, y: Double, width: Double, height: Double, text: String)(implicit stateId: Long): Future[Sheet] =
    wallManager.createSheet(wallId, x, y, width, height, text)

  def deleteSheetInWall(wallId: Long, sheetId: Long)(implicit stateId: Long): Future[Boolean] =
    wallManager.deleteSheet(wallId, sheetId)

  def subscribeWall(wallId: Long)(implicit stateId: Long): Future[Boolean] =
    wallManager.subscribe(wallId)

  def addOnWallUpdateEventHandler(wallId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit =
    wallManager.addOnUpdateEventHandler(wallId, handler)

  def clear(): Unit
}
