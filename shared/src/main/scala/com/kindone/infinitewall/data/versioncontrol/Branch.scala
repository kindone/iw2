package com.kindone.infinitewall.data.versioncontrol

import com.kindone.util.{Hasher, Time}

import scala.util.Random

/**
 * Created by kindone on 2016. 9. 16..
 */
object Branch
{
  def genHash = {
    val time = Time.now()
    val rand = Random.nextLong()
    val identifier = time + " " + rand.toString
    Hasher.SHA1(identifier)
  }

  def create() = {
    Branch(genHash)
  }
}

case class Branch(hash:String)