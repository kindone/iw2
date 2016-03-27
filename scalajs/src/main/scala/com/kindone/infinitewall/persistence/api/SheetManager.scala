package com.kindone.infinitewall.persistence.api

import com.kindone.infinitewall.data.Sheet

import scala.concurrent.Future

/**
 * Created by kindone on 2016. 3. 19..
 */
trait SheetManager {
  def get(id: Long): Future[Sheet]

  def create(x: Double, y: Double, width: Double, height: Double, text: String): Future[Sheet]

  def delete(id: Long): Future[Boolean]

  def move(id: Long, x: Double, y: Double): Future[Boolean]

  def resize(id: Long, width: Double, height: Double): Future[Boolean]

  def setDimension(id: Long, x: Double, y: Double, width: Double, height: Double): Future[Boolean]

  def setText(id: Long, text: String): Future[Boolean]
}
