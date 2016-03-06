package com.kindone.infinitewall

/**
 * Created by kindone on 2016. 3. 5..
 */
trait Synchronizable {
  val uri:URI
  val origin:Option[URI] = None

  def isAuto:Boolean
  def isManual = !isAuto
}
