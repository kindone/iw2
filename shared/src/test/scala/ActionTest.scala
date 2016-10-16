package shared.test

import com.kindone.infinitewall.data.action.ChangeSheetContentAction
import com.kindone.infinitewall.data.versioncontrol.{Branch}
import com.kindone.infinitewall.data.versioncontrol.util.{TextOperation, StringWithHistory}
import minitest._

object ActionTest extends SimpleTestSuite {
  test("action") {
    val base = "Hello world"
    val ac1 = ChangeSheetContentAction(0, "Hi", 0, 5)
    val ac2 = ChangeSheetContentAction(0, "abcd", 2, 4)

    val ss = new StringWithHistory(base)
    val (ss2, _) = ss.applyTextOperation(TextOperation(ac1.content, ac1.from, ac1.numDeleted), "0")
    println(ss2.text)
    assertEquals(ss2.text, "Hi world")
  }
}
