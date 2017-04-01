package com.kindone.infinitewall.elements

import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.elements.events._
import com.kindone.infinitewall.event._
import com.kindone.infinitewall.facades.ShowdownConverter
import com.kindone.infinitewall.persistence.api.Persistence
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent
import org.scalajs.jquery._
import scala.scalajs.js
import org.scalajs.dom
import scalatags.JsDom.all._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

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

    implicit val mockStateId: Long = 0L

    val wallModelFuture = {
      for (requestedWall <- persistence.getWall(id)) yield {
        if (!requestedWall.isEmpty) {
          requestedWall.get
        } else {
          throw new RuntimeException("Unable to find wall with id: " + id)
        }
      }
    }

    for (wallModel <- wallModelFuture) {

      // create views
      val wall = new Wall(wallModel)
      val controlPad = new ControlPad

      element.append(wall.element)
      wall.setup()

      element.append(controlPad.element)
      controlPad.setup()

      element.append(overlay)

      // attach editor
      val showdownConverter = new ShowdownConverter()
      val editor = new Editor(showdownConverter)
      element.append(editor.element)
      editor.setup()

      lazy val editorClose: Function1[JQueryEventObject, Unit] = { evt: JQueryEventObject =>
        editor.close()
        overlay.hide()
        overlay.off("mousedown", editorClose)
      }

      // sheets
      def createSheet(sheetModel: Sheet) = {
        val sheet = new Sheet(sheetModel, showdownConverter)
        wall.appendSheet(sheet)

        // activate events
        sheet.addOnDimensionChangeListener({ evt: SheetDimensionChangeEvent =>
          persistence.setSheetDimension(sheet.id, evt.x, evt.y, evt.w, evt.h)
        })

        sheet.addOnContentChangeListener({ evt: SheetContentChangeEvent =>
          persistence.setSheetText(sheet.id, evt.content)
        })

        sheet.addOnSheetCloseListener({ evt: SheetCloseEvent =>
          wall.removeSheet(evt.sheet)
        })

        sheet.setOnDoubleClickListener((sheet: Sheet) => {
          editor.open(sheet)
          editor.focus()
          overlay.show()
          overlay.on("mousedown", editorClose)
        })

        persistence.addOnSheetUpdateEventHandler(sheet.id, { evt: PersistenceUpdateEvent =>
          dom.console.info("sheet persistence event:" + evt.toString)
          evt.change.action match {
            // TODO
            case MoveSheetAction(id, x, y) =>
            case ResizeSheetAction(id, width, height) =>
            case ChangeSheetDimensionAction(id, x, y, width, height) =>
            case ChangeSheetContentAction(id, content, pos, length) =>
            case _ =>
          }
        })
        persistence.subscribeSheet(sheet.id)
      }

      // load and create sheets
      for (
        sheets <- persistence.getSheetsInWall(wallModel.id);
        sheetId <- sheets;
        sheet <- persistence.getSheet(sheetId)
      ) {
        createSheet(sheet)
      }

      // activate sheet lifecycle events
      wall.addOnSheetRemovedListener({ evt: SheetRemovedEvent =>
        persistence.deleteSheetInWall(wall.id, evt.sheetId)
      })

      wall.addOnViewChangedListener({ evt: ViewChangeEvent =>
        persistence.setWallView(wall.id, evt.x, evt.y, evt.scale)
      })

      // activate controlpad events
      controlPad.setOnAddButtonClickListener({ () =>
        // create random sheet model first and realize
        //dom.console.info("creating random sheet:" + wall.center)
        for (sheetModel <- persistence.createSheetInWall(wall.id, js.Math.random() * 800, js.Math.random() * 600, 100, 100, ""))
          createSheet(sheetModel)
      })

      controlPad.setOnClearDBButtonClickListener({ () =>
        persistence.clear()
      })

      // add wall persistence update event
      persistence.addOnWallUpdateEventHandler(wall.id, { evt: PersistenceUpdateEvent =>
        dom.console.info("wall persistence event:" + evt.toString)
        evt.change.action match {
          //TODO
          case ChangeTitleAction(_, _) =>
          case CreateSheetAction(_, _) =>
          case DeleteSheetAction(_, _) =>
          case _                       =>
        }
      })

      persistence.subscribeWall(wall.id)(wallModel.stateId).foreach { result =>
        dom.console.info("now subscribing wall: " + wall.id)
      }

    }

  }

}
