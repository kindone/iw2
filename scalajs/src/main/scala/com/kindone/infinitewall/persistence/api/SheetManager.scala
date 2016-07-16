package com.kindone.infinitewall.persistence.api

import com.kindone.infinitewall.data.Sheet
import com.kindone.infinitewall.events.EventListener
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent

import scala.concurrent.Future

/**
 * Created by kindone on 2016. 3. 19..
 */
trait SheetManager {
  def get(sheetId: Long): Future[Sheet]

  def move(sheetId: Long, x: Double, y: Double): Future[Boolean]

  def resize(sheetId: Long, width: Double, height: Double): Future[Boolean]

  def setDimension(sheetId: Long, x: Double, y: Double, width: Double, height: Double): Future[Boolean]

  def setText(sheetId: Long, text: String): Future[Boolean]

  def subscribe(sheetId: Long): Future[Boolean] = {
    // override
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
    Future(true)
  }

  def addOnUpdateEventHandler(sheetId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    // override
  }
}
