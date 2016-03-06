package com.kindone.infinitewall

/**
 * Created by kindone on 2016. 3. 5..
 */
class LocalWall(val path:String) extends Synchronizable{
  val uri = URI("wall", path)
  def isAuto = true
}
