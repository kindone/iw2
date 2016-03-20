package com.kindone.infinitewall.persistence.remotestorage

import com.kindone.infinitewall.persistence.Sheet
import com.kindone.infinitewall.persistence.api.{SheetManager => SheetManagerAPI}
import org.scalajs.dom
import dom.ext.Ajax
import upickle.default._

import scala.concurrent.Future

/**
 * Created by kindone on 2016. 3. 20..
 */
class SheetManager(site:String) extends SheetManagerAPI{
  def get(id: Long): Future[Sheet] = {
    for(response <- Ajax.get(site +
      s"/sheet/$id")) yield
      read[Sheet](response.responseText)
  }

  def create(x: Double, y: Double, width: Double, height: Double, text: String): Future[Sheet] = {
    val sheet = new Sheet(0, x, y, width, height, text)
    for(response <- Ajax.post(site +
      s"/sheet", write[Sheet](sheet))) yield
      read[Sheet](response.responseText)
  }

  def delete(id: Long): Future[Boolean] = {
    for(response <- Ajax.delete(site +
      s"/sheet/$id")) yield
      response.status == 200
  }

  def move(id: Long, x: Double, y: Double): Future[Boolean] = {
    val sheet = new Sheet(0, x, y, 0, 0, 0)
    for(response <- Ajax.put(site +
      s"/movesheet/$id", write[Sheet](sheet))) yield
      response.status == 200
  }

  def resize(id: Long, width: Double, height: Double): Future[Boolean] = {
    val sheet = new Sheet(0, 0, 0, width, height, "")
    for(response <- Ajax.put(site +
      s"/resizesheet/$id", write[Sheet](sheet))) yield
      response.status == 200
  }

  def setDimension(id: Long, x: Double, y: Double, width: Double, height: Double): Future[Boolean] = {
    val sheet = new Sheet(0, x, y, width, height, "")
    for(response <- Ajax.put(site +
      s"/setsheetdimension/$id", write[Sheet](sheet))) yield
      response.status == 200
  }

  def setText(id: Long, text: String): Future[Boolean] = {
    val sheet = new Sheet(0, 0, 0, 0, 0, text)
    for(response <- Ajax.put(site +
      s"/setsheeettext/$id", write[Sheet](sheet))) yield
      response.status == 200
  }
}
