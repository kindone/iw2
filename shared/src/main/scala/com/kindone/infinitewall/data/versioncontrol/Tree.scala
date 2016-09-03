package com.kindone.infinitewall.data.versioncontrol

/**
 * Created by kindone on 2016. 8. 27..
 */
class Tree {
  private var changes:Map[String, Change] = Map() // index: hash -> change
  private var parentHashMap:Map[String, String] = Map() // index: parentHash -> hash
  private var head:Option[Change] = None // TODO
  private var snapshots:Map[String, Snapshot] = Map() // initial + optimization

  def find() = {}

  def insert(change:Change) = {
    if(changes.isEmpty)
    {

    }
    else {

    }
  }


  private def initialize(snapshot:Snapshot) = {
    snapshots = (snapshots)
  }

}
