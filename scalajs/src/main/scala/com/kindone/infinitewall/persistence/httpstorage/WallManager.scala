package com.kindone.infinitewall.persistence.httpstorage

import com.kindone.infinitewall.data.Wall
import com.kindone.infinitewall.data.state.{Wall, Sheet}
import com.kindone.infinitewall.persistence.api.{ WallManager => WallManagerAPI }
import org.scalajs.dom.ext.Ajax
import upickle.default._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

import scala.concurrent.Future

/**
 * Created by kindone on 2016. 3. 20..
 */
class WallManager(site: String) extends WallManagerAPI {
  def create(title: String, x: Double = 0, y: Double = 0, scale: Double = 1.0): Future[Wall] = {
    val wall = new Wall(0, 0, x, y, scale, title)
    for (
      response <- Ajax.post(site +
        s"/wall", write[Wall](wall), headers = Map("X-Requested-With" -> "XMLHttpRequest"))
    ) yield read[Wall](response.responseText)
  }

  def delete(id: Long): Future[Boolean] = {
    for (
      response <- Ajax.delete(site +
        s"/wall/$id")
    ) yield response.status == 200
  }

  def get(id: Long): Future[Option[Wall]] = {
    for (
      response <- Ajax.get(site +
        s"/wall/$id")
    ) yield {
      if (response.status == 200)
        Some(read[Wall](response.responseText))
      else
        None
    }
  }

  def getWalls(): Future[Seq[Wall]] = {
    for (
      response <- Ajax.get(site +
        s"/wall")
    ) yield read[Seq[Wall]](response.responseText)
  }

  def pan(id: Long, x: Double, y: Double): Future[Boolean] = {
    val wall = new Wall(id, 0, x, y, 0)
    for (
      response <- Ajax.put(site +
        s"/wall/$id/pan", write[Wall](wall))
    ) yield response.status == 200
  }

  def zoom(id: Long, scale: Double): Future[Boolean] = {
    val wall = new Wall(id, 0, 0, 0, scale)
    for (
      response <- Ajax.put(site +
        s"/wall/$id/zoom", write[Wall](wall))
    ) yield response.status == 200
  }

  def setView(id: Long, x: Double, y: Double, scale: Double): Future[Boolean] = {
    val wall = new Wall(id, 0, x, y, scale)
    for (
      response <- Ajax.put(site +
        s"/wall/$id/view", write[Wall](wall))
    ) yield response.status == 200
  }

  def setTitle(id: Long, title: String): Future[Boolean] = {
    val wall = new Wall(id, 0, 0, 0, 0, title)
    for (
      response <- Ajax.put(site +
        s"/wall/$id/title", write[Wall](wall))
    ) yield response.status == 200
  }

  def getSheets(wallId: Long): Future[Set[Long]] = {
    for (
      response <- Ajax.get(site +
        s"/wall/$wallId/sheet")
    ) yield read[Set[Long]](response.responseText)
  }

  def createSheet(wallId: Long, x: Double, y: Double, width: Double, height: Double, text: String): Future[Sheet] = {
    val sheet = new Sheet(0, 0, x, y, width, height, text)
    for (
      response <- Ajax.post(site +
        s"/wall/$wallId/sheet", write(sheet), headers = Map("X-Requested-With" -> "XMLHttpRequest"))
    ) yield read[Sheet](response.responseText)
  }

  def deleteSheet(id: Long, sheetId: Long): Future[Boolean] = {
    for (
      response <- Ajax.delete(site +
        s"/wall/$id/sheet/$sheetId")
    ) yield response.status == 200
  }
}
