package com.kindone.infinitewall.persistence.wsstorage

import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.{ Sheet, Wall }
import com.kindone.infinitewall.events.EventListener
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent
import com.kindone.infinitewall.persistence.api.{ WallManager => WallManagerAPI }
import com.kindone.infinitewall.persistence.wsstorage.events.{ WebSocketEventDispatcher, SocketOpenCloseEvent, WebSocketEvent }

import scala.concurrent.{ Promise, Future }
import upickle.default._

/**
 * Created by kindone on 2016. 4. 17..
 */
class WallManager(socket: Socket) extends WallManagerAPI {

  def create(title: String, x: Double = 0, y: Double = 0, scale: Double = 1.0): Future[Wall] = {
    socket.send[Wall](CreateWallAction(title, x, y, scale))
  }

  def delete(wallId: Long): Future[Boolean] = {
    socket.send[Boolean](DeleteWallAction(wallId))
  }

  def get(wallId: Long): Future[Option[Wall]] = {
    socket.send[Option[Wall]](GetWallAction(wallId))
  }

  def getWalls(): Future[Seq[Wall]] = {
    socket.send[Seq[Wall]](ListWallAction())
  }

  def pan(wallId: Long, x: Double, y: Double): Future[Boolean] = {
    socket.send[Boolean](ChangePanAction(wallId, x, y))
  }

  def zoom(wallId: Long, scale: Double): Future[Boolean] = {
    socket.send[Boolean](ChangeZoomAction(wallId, scale))
  }

  def setView(wallId: Long, x: Double, y: Double, scale: Double): Future[Boolean] = {
    socket.send[Boolean](ChangeViewAction(wallId, x, y, scale))
  }

  def setTitle(wallId: Long, title: String): Future[Boolean] = {
    socket.send[Boolean](ChangeTitleAction(wallId, title))
  }

  def getSheets(wallId: Long): Future[Set[Long]] = {
    socket.send[Set[Long]](ListSheetAction(wallId))
  }

  def createSheet(wallId: Long, x: Double, y: Double, width: Double, height: Double, text: String): Future[Sheet] = {
    socket.send[Sheet](CreateSheetAction(wallId, Sheet(0, 0, x, y, width, height, text)))
  }

  def deleteSheet(wallId: Long, sheetId: Long): Future[Boolean] = {
    socket.send[Boolean](DeleteSheetAction(wallId, sheetId))
  }

  override def subscribe(wallId: Long): Future[Boolean] = {
    socket.send[Boolean](SubscribeWallEventAction(wallId))
  }

  override def addOnUpdateEventHandler(wallId: Long, handler: EventListener[PersistenceUpdateEvent]): Unit = {
    socket.addOnWallNotificationListener(wallId, handler)
  }
}
