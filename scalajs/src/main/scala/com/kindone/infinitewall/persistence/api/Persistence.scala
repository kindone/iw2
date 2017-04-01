package com.kindone.infinitewall.persistence.api

import com.kindone.infinitewall.data.{ Wall, Sheet }
import com.kindone.infinitewall.event.EventListener
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent

import scala.concurrent.Future

/**
 * Created by kindone on 2016. 3. 28..
 */
trait Persistence {

  /* sheet */
  def getSheet(sheetId: Long)(implicit stateId: Long): Future[Sheet]

  def moveSheet(sheetId: Long, x: Double, y: Double)(implicit stateId: Long): Future[Boolean]

  def resizeSheet(sheetId: Long, width: Double, height: Double)(implicit stateId: Long): Future[Boolean]

  def setSheetDimension(sheetId: Long, x: Double, y: Double, width: Double, height: Double)(implicit stateId: Long): Future[Boolean]

  def setSheetText(sheetId: Long, text: String)(implicit stateId: Long): Future[Boolean]

  def subscribeSheet(sheetId: Long)(implicit stateId: Long): Future[Boolean]
  def addOnSheetUpdateEventHandler(sheetId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit

  /* wall */
  def createWall(title: String, x: Double = 0, y: Double = 0, scale: Double = 1.0): Future[Wall]

  def deleteWall(wallId: Long): Future[Boolean]

  def getWall(wallId: Long): Future[Option[Wall]]

  def getWalls(): Future[Seq[Wall]]

  def panWall(wallId: Long, x: Double, y: Double)(implicit stateId: Long): Future[Boolean]

  def zoomWall(wallId: Long, scale: Double)(implicit stateId: Long): Future[Boolean]

  def setWallView(wallId: Long, x: Double, y: Double, scale: Double)(implicit stateId: Long): Future[Boolean]

  def setWallTitle(wallId: Long, title: String)(implicit stateId: Long): Future[Boolean]

  def getSheetsInWall(wallId: Long)(implicit stateId: Long): Future[Set[Long]]

  def createSheetInWall(wallId: Long, x: Double, y: Double, width: Double, height: Double, text: String)(implicit stateId: Long): Future[Sheet]

  def deleteSheetInWall(wallId: Long, sheetId: Long)(implicit stateId: Long): Future[Boolean]

  def subscribeWall(wallId: Long)(implicit stateId: Long): Future[Boolean]

  def addOnWallUpdateEventHandler(wallId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit

  def clear(): Unit
}
