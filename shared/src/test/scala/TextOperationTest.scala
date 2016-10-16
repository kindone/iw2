package shared.test

import com.kindone.infinitewall.data.versioncontrol.util.{TextOperation, StringWithHistory}
import minitest._

object TextOperationTest extends SimpleTestSuite {
  test("single branch") {
    val ss = new StringWithHistory("abcdefg")
    val op1 = TextOperation("A", 1, 1)
    val (ss2,_) = ss.applyTextOperation(op1, "0")
    assertEquals(ss2.text, "aAcdefg")
  }

  test("double branches") {
    val ss = new StringWithHistory("abcdefg")
    val op1 = TextOperation("A", 1, 1)
    val (ss2, _) = ss.applyTextOperation(op1, "0")

    val op2 = TextOperation("A", 1, 1)
    val (ss3,_) = ss2.applyTextOperation(op2, "1")
    assertEquals(ss3.text, "aAAcdefg")
  }

  test("double branches - crossed") {
    val ss = new StringWithHistory("abcdefg")
    val opA1 = TextOperation("A", 1, 1) // replace 'b'
    val opA2 = TextOperation("A", 1, 1) // replace 'b'
    val opA3 = TextOperation("B", 2, 1) // replace 'c'
    val (rs1,_) = ss.applyTextOperation(opA1, "0")
    val (rs2,_) = rs1.applyTextOperation(opA2, "1")
    val (rs3,_) = rs2.applyTextOperation(opA3, "0")
    // "aABdefg"
    assertEquals(rs3.text, "aAABdefg")
//    println(rs1.toString())
//    println(rs2.toString())
//    println(rs3.toString())

    val opB1 = TextOperation("A", 1, 1) // same as opA2 aAcdefg
    val opB2 = TextOperation("D", 2, 1) // replace 'c'  aADdefg
//    ss.apply(opB1, Set(0,1))  // implicitly by opA2
    val (ss2,_) = rs3.applyTextOperation(opB2, "1")
    assertEquals(ss2.text, "aAABDdefg")
  }


//  test("should throw") {
//    class DummyException extends RuntimeException("DUMMY")
//    def test(): String = throw new DummyException
//
//    intercept[DummyException] {
//      test()
//    }
//  }
//
//  test("test result of") {
//    assertResult("hello world") {
//      "hello" + " " + "world"
//    }
//  }
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
