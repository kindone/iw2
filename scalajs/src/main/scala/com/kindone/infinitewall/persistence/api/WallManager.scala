package com.kindone.infinitewall.persistence.api

import com.kindone.infinitewall.data.{ Sheet, Wall }
import com.kindone.infinitewall.event.EventListener
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent

import scala.concurrent.Future

/**
 * Created by kindone on 2016. 3. 19..
 */
trait WallManager {

  def create(title: String, x: Double = 0, y: Double = 0, scale: Double = 1.0): Future[Wall]

  def delete(wallId: Long): Future[Boolean]

  def get(wallId: Long): Future[Option[Wall]]

  def getWalls(): Future[Seq[Wall]]

  def pan(wallId: Long, x: Double, y: Double): Future[Boolean]

  def zoom(wallId: Long, scale: Double): Future[Boolean]

  def setView(wallId: Long, x: Double, y: Double, scale: Double): Future[Boolean]

  def setTitle(wallId: Long, title: String): Future[Boolean]

  def getSheets(wallId: Long): Future[Set[Long]]

  def createSheet(wallId: Long, x: Double, y: Double, width: Double, height: Double, text: String): Future[Sheet]

  def deleteSheet(wallId: Long, sheetId: Long): Future[Boolean]

  def subscribe(wallId: Long): Future[Boolean] = {
    // override
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
    Future(true)
  }

  def addOnUpdateEventHandler(wallId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    // override
  }

}