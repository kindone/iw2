package com.kindone.infinitewall.data.versioncontrol.util

/**
 * Created by kindone on 2016. 10. 2..
 */
// immutable
class JournaledString(list: List[CharWithMod]) {
  // initialize

  def this(str: String) = {
    this(str.map { c =>
      new CharWithMod(c)
    }.toList)
  }

  // @description: apply op operation and returns its transformed operation
  def applyTextOperation(op: TextOperation, branchName: String): (JournaledString, TextOperation) = {
    var i = 0
    var nVisible = 0
    var insertPos = 0
    var alteredFrom = 0
    var numDeleted = 0
    var iAtVisible = 0 // last visible char in list

    // mark deleted
    val newList = list.map { cs =>
      // if character is visible
      if (!cs.deletedBy.contains(branchName) && (cs.insertedBy.isEmpty || cs.insertedBy.contains(branchName))) {
        // delete in range
        if (nVisible >= op.from && nVisible < op.from + op.numDeleted) {
          if (cs.deletedBy.isEmpty) // if char is being deleted first time
            numDeleted += 1
          cs.deletedBy += branchName
          insertPos = i
        } else if (nVisible == op.from + op.numDeleted) {
          insertPos = i
        }
        nVisible += 1
        iAtVisible = i + 1
      }
      i += 1
      cs
    }

    // insert at the end of list
    if (nVisible <= op.from)
      insertPos = iAtVisible

    // inserted part
    val inserted = op.content.map { c =>
      new CharWithMod(c, Set(branchName))
    }

    i = 0
    for(cs <- list)
    {
      if (i < insertPos && cs.deletedBy.isEmpty)
          alteredFrom += 1
      i += 1
    }

    val finalList = list.take(insertPos) ++ inserted.toList ++ list.drop(insertPos)
    val alteredOp = TextOperation(op.content, alteredFrom, numDeleted)
    (new JournaledString(finalList), alteredOp)
  }

  // @description current state of string as text (deletion applied)
  def text = {
    list.flatMap { cs =>
      if (cs.deletedBy.isEmpty)
        Some(cs.char)
      else
        None
    }.mkString
  }

  override def toString = {
    list.foldLeft("") { (str, c) =>
      str +
      c.char.toString + "(insertedBy: " +
      c.insertedBy.toString() + ", deletedBy: " +
      c.deletedBy.toString() + ") "
    }
  }
}
