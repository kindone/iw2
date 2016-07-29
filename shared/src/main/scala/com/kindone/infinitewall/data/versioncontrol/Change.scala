package com.kindone.infinitewall.data.versioncontrol

/**
 * Created by kindone on 2016. 7. 24..
 */
// json content to hash.
case class Change(hash:String, changeType:String, content:String, parentHash:Option[String])

