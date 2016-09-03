package com.kindone.infinitewall.data.versioncontrol.util

object TextOperation {
  val blank = TextOperation(0, 0, "")
}

// "replace a text from position 'from' by length 'length' with content"
case class TextOperation(from: Int, length: Int, content: String) {

  // apply text operation on a string
  def transform(str: String) = {
    val p1 = Math.min(Math.max(0, from), str.length)
    val p2 = Math.min(Math.max(0, from + length), str.length)

    assert(p1 == from)
    assert(p2 == from + length)

    str.substring(0, p1) + content + str.substring(p2, str.length)
  }

  // apply text operation and create undo
  def transformAndCreateUndo(str: String) = {
    val p1 = Math.min(Math.max(0, from), str.length)
    val p2 = Math.min(Math.max(0, from + length), str.length)

//    assert(p1 == from, { Logger.error(s"p1($p1) != from($from)") })
//    assert(p2 == from + length, { Logger.error(s"p2($p2) != from + length($from + $length)") })

    val alteredText = str.substring(0, p1) + content + str.substring(p2, str.length)
    val undoOp = {
      val remove_ = content.length
      val p1_ = p1
      val p2_ = Math.min(Math.max(0, from + remove_), alteredText.length)
      val content_ = str.substring(p1, p2)
      val originalText = alteredText.substring(0, p1_) + content_ + alteredText.substring(p2_, alteredText.length)
      assert(str == originalText)
      TextOperation(p1_, remove_, content_)
    }

    (alteredText, undoOp)
  }

  override def toString() = "[from: " + from + ", remove: " + length + ", content: " + content + "]"

}


// char with marker inserted/deleted by branch
class CharWithState(val char: Char, val insertedBy: Set[Int] = Set(), var deletedBy: Set[Int] = Set())

class StringWithState(str: String) {
  // initialize
  var list: List[CharWithState] = str.map { c =>
    new CharWithState(c)
  }.toList

  // @description: apply op operation and returns its transformed operation
  def apply(op: TextOperation, branch: Int): TextOperation = {
    var i = 0
    var iBranch = 0
    var insertPos = 0
    var alteredFrom = 0
    var numDeleted = 0
    var iAtIBranch = 0

    list = list.map { cs =>
      if (!cs.deletedBy.contains(branch) && (cs.insertedBy.isEmpty || cs.insertedBy == Set(branch))) {
        if (iBranch >= op.from && iBranch < op.from + op.length) {
          if (cs.deletedBy.isEmpty)
            numDeleted += 1
          cs.deletedBy += branch
          insertPos = i
        } else if (iBranch == op.from + op.length) {
          insertPos = i
        }
        iBranch += 1
        iAtIBranch = i + 1
      }
      i += 1
      cs
    }

    if (iBranch <= op.from)
      insertPos = iAtIBranch

    val inserted = op.content.map { c =>
      new CharWithState(c, Set(branch))
    }

    i = 0
    for(cs <- list)
    {
      if (i < insertPos) {
        if (cs.deletedBy.isEmpty)
          alteredFrom += 1
      }
      i += 1
    }

    list = list.take(insertPos) ++ inserted.toList ++ list.drop(insertPos)
    TextOperation(alteredFrom, numDeleted, op.content)
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
}

//object TextOperation extends App {
//  val A = Array(Operation(2, 2, "in"), Operation(2, 2, ""), Operation(0, 0, "newlyInserted"), Operation(1, 3, ""))
//  val B = Array(Operation(2, 2, "or"), Operation(3, 1, "R"), Operation(0, 3, ""))
//  val base = new StringWithState("baseText")
//
//  //	println(base.text)
//  //	for(a <- A) {
//  //		val a2 = base(a, 0)
//  //		if(a != a2)
//  //			println("altered op: " + a + " => " + a2)
//  //		println(base.text)
//  //		//println(base.html)
//  //	}
//  //
//  //	for(b <- B) {
//  //		val b2 = base(b, 1)
//  //		if(b != b2)
//  //			println("altered op: " + b + " => " + b2)
//  //		println(base.text)
//  //		//println(base.html)
//  //	}
//
//}
