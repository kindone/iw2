package com.kindone.infinitewall.elements

import com.kindone.infinitewall.events._
import com.kindone.infinitewall.facades.ShowdownConverter
import org.scalajs.jquery._
import scala.scalajs.js
import com.kindone.infinitewall.persistence.{ Wall => WallModel, Sheet => SheetModel, _ }
import scalatags.JsDom.all._

/**
 * Created by kindone on 2016. 3. 5..
 */
class WallView(id: Long, persistence: Persistence) extends Element {
  val element = {
    val html = div(cls := "wallview")()
    jQuery(html.render)
  }

  val overlay = {
    val html = div(cls := "overlay-transparent")()
    jQuery(html.render)
  }

  def setup() = {

    val wallModel = {
      val requestedWall = persistence.wallManager.get(id)
      if (!requestedWall.isEmpty) {
        requestedWall.get
      } else {
        throw new RuntimeException("Unable to find wall with id: " + id)
      }
    }

    val wallManager = persistence.wallManager
    val sheetManager = persistence.sheetManager

    val wall = new Wall(wallModel)
    val controlPad = new ControlPad

    element.append(wall.element)
    wall.setup()

    element.append(controlPad.element)
    controlPad.setup()

    element.append(overlay)

    val showdownConverter = new ShowdownConverter()
    val editor = new Editor(showdownConverter)
    element.append(editor.element)
    editor.setup()

    lazy val editorClose: js.Function1[JQueryEventObject, js.Any] = (evt: JQueryEventObject) => {
      editor.close()
      overlay.hide()
      overlay.off("mousedown", editorClose)
    }

    def appendSheetToWall(sheetModel: SheetModel) = {
      val sheet = new Sheet(sheetModel, showdownConverter)
      wall.appendSheet(sheet)

      // activate events
      sheet.addOnDimensionChangeListener(new EventListener[SheetDimensionChangeEvent] {
        def apply(evt: SheetDimensionChangeEvent) = {
          sheetManager.setDimension(sheet.id, evt.x, evt.y, evt.w, evt.h)
        }
      })
      sheet.addOnContentChangeListener(new EventListener[SheetContentChangeEvent] {
        def apply(evt: SheetContentChangeEvent) = {
          sheetManager.setText(sheet.id, evt.content)
        }
      })
      sheet.addOnSheetCloseListener(new EventListener[SheetCloseEvent] {
        def apply(evt: SheetCloseEvent) = {
          wall.removeSheet(evt.sheet)
          sheetManager.delete(evt.sheetId)
        }
      })

      sheet.setOnDoubleClickListener((sheet: Sheet) => {
        editor.open(sheet)
        overlay.show()
        overlay.on("mousedown", editorClose)
      })

    }

    val sheets = wallManager.getSheets(wallModel.id)
    for (sheetId <- sheets) {
      appendSheetToWall(sheetManager.get(sheetId))
    }

    wall.addOnSheetAppendedListener(new EventListener[SheetAppendedEvent] {
      def apply(evt: SheetAppendedEvent) = {
        wallManager.appendSheet(wall.id, evt.sheetId)
      }
    })
    wall.addOnSheetRemovedListener(new EventListener[SheetRemovedEvent] {
      def apply(evt: SheetRemovedEvent) = {
        wallManager.appendSheet(wall.id, evt.sheetId)
      }
    })
    wall.addOnViewChangedListener(new EventListener[ViewChangeEvent] {
      def apply(evt: ViewChangeEvent) = {
        wallManager.setView(wall.id, evt.x, evt.y, evt.scale)
      }
    })

    wall.addOnSheetRemovedListener(new EventListener[SheetRemovedEvent] {
      def apply(evt: SheetRemovedEvent) = {
        wallManager.removeSheet(wall.id, evt.sheetId)
      }
    })

    controlPad.setOnAddButtonClickListener({ () =>
      // create random sheet
      val sheetModel = sheetManager.create(js.Math.random() * 800, js.Math.random() * 800, 100, 100, "")
      appendSheetToWall(sheetModel)
    })

    controlPad.setOnClearDBButtonClickListener({ () =>
      persistence.clear()
    })
  }

}
