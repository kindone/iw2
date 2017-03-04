package com.kindone.infinitewall.util

import java.util.UUID

import com.kindone.util.Time

/**
 * Created by kindone on 2017. 2. 18..
 */

trait Timer {
  def setTimeout(timeoutMs: Long)(task: => Unit):UUID
  def clearTimeout(uuid:UUID):Unit
}

case class TimerEntry(task: () => Unit, timestampMs:Long)

class MockTimer extends Timer{
  var scheduledEntries:Map[UUID, TimerEntry] = Map()
  var currentTimestampMs: Long = 0

  override def setTimeout(timeoutMs: Long)(task: => Unit): UUID = {
    val uuid = UUID.randomUUID()
    // convert by-name param to a function
    println("timer " + uuid + " scheduled at: " + (timeoutMs+currentTimestampMs) + " (currentTime:" + currentTimestampMs + ")")
    scheduledEntries += (uuid -> TimerEntry(() => task, currentTimestampMs + timeoutMs))
    uuid
  }

  override def clearTimeout(uuid: UUID): Unit = {

    scheduledEntries.get(uuid).foreach { entry =>
      // cancel?
      println("timer " + uuid + " cleared scheduled at: " + entry.timestampMs + " (currentTime:" + currentTimestampMs + ")")
      canceledEntries :+= entry
    }

    scheduledEntries -= uuid

  }

  def advance(timeMs:Long) = {
    println("timer advancing to: " + (currentTimestampMs + timeMs))
    val fired = scheduledEntries.filter(entry => entry._2.timestampMs <= currentTimestampMs+timeMs).toSeq
    val startTimeMs = currentTimestampMs
    val endTimeMs = currentTimestampMs +timeMs

    fired.sortWith(_._2.timestampMs < _._2.timestampMs).foreach { entry =>
      println("timer " + entry._1 + " firing at: " + entry._2.timestampMs + " (time flowing from: " + startTimeMs + " to: " + endTimeMs +")")
      currentTimestampMs = entry._2.timestampMs
      scheduledEntries -= entry._1
      entry._2.task()
      firedEntries :+= entry._2
    }

    currentTimestampMs = endTimeMs
  }

  var firedEntries:List[TimerEntry] = List()
  var canceledEntries:List[TimerEntry] = List()
}

//
//class ConcreteTimer extends Timer {
//
//  var timers:Map[UUID, Timer] = Map()
//
//  def schedule(callback:TimerCallback, fireAtMsLater:Long):UUID =  {
//    val timer:Timer = new ConcreteTimer()
//    val uuid:UUID = UUID.randomUUID()
//    timer.schedule(callback, fireAtMsLater)
//    timers = timers + (uuid -> timer)
//    uuid
//  }
//
//  def getDate():Long = {
//    return Time.now
//  }
//
//  def getTimers() = {
//     timers;
//  }
//
//  def cancel(uuid:UUID) = {
//    val timer:Option[Timer] = timers.get(uuid);
//    timers = timers - uuid
//    timer.foreach(_.cancel(uuid))
//  }
//}