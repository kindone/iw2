package com.kindone.infinitewall.persistence.api

import com.kindone.infinitewall.data.{ Sheet, Wall }

import scala.concurrent.Future

/**
 * Created by kindone on 2016. 3. 19..
 */
trait WallManager {

  def create(title: String, x: Double = 0, y: Double = 0, scale: Double = 1.0): Future[Wall]

  def delete(id: Long): Future[Boolean]

  def get(id: Long): Future[Option[Wall]]

  def getWalls(): Future[Seq[Wall]]

  def pan(id: Long, x: Double, y: Double): Future[Boolean]

  def zoom(id: Long, scale: Double): Future[Boolean]

  def setView(id: Long, x: Double, y: Double, scale: Double): Future[Boolean]

  def getSheets(wallId: Long): Future[Set[Long]]

  def createSheet(id: Long, x: Double, y: Double, width: Double, height: Double, text: String): Future[Sheet]

  def deleteSheet(id: Long, sheetId: Long): Future[Boolean]

}