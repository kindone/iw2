package com.kindone.infinitewall.data.versioncontrol.util

object  TextOperation {
  val blank = TextOperation("", 0, 0)
}

// "replace a text from position 'from' by length 'length' with content"
case class TextOperation(content: String, from: Int = 0, numDeleted: Int = -1) {

  // apply text operation on a string
  def transform(str: String):String = {
    val p1 = Math.min(Math.max(0, from), str.length)
    val p2 = Math.min(Math.max(0, from + numDeleted), str.length)

    assert(p1 == from)
    assert(p2 == from + numDeleted)

    str.substring(0, p1) + content + str.substring(p2, str.length)
  }

  // apply text operation and create undo
  def transformAndCreateUndo(str: String):Tuple2[String, TextOperation] = {
    val p1 = Math.min(Math.max(0, from), str.length)
    val p2 = Math.min(Math.max(0, from + numDeleted), str.length)

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
      TextOperation(content_, p1_, remove_)
    }

    (alteredText, undoOp)
  }

  override def toString() = "[content: " + content + ", from: " + from + ", remove: " + numDeleted + "]"

}




