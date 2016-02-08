package facades

import scala.scalajs.js
import org.scalajs.dom.raw.Element

/**
 * Created by kindone on 2016. 1. 31..
 */
@js.native
object CodeMirror extends js.Object {
  def apply(element: Element, options: js.Dictionary[Any]): CodeMirror = js.native
}

@js.native
class CodeMirror extends js.Object {
  def refresh() = js.native
}