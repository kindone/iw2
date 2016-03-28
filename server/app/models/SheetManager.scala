package models

import play.api.Play.current
import anorm.Row
import com.kindone.infinitewall.data.{ Sheet, Wall }
import play.api.db.DB
import anorm._

/**
 * Created by kindone on 2016. 3. 27..
 */
class SheetManager {
  def find(id: Long) = DB.withConnection { implicit c =>
    SQL"select id, x, y, width, height, content from sheets where id = $id".map {
      case Row(id: Long, x: Double, y: Double,
        width: Double, height: Double, content: java.sql.Clob) => Sheet(id, x, y, width, height, content.getSubString(1, content.length.asInstanceOf[Int]))
    }.singleOpt
  }

  def delete(id: Long) = DB.withConnection { implicit c =>
    SQL"delete from sheets where id = $id".executeUpdate()
  }

  def setPosition(id: Long, x: Double, y: Double) =
    DB.withConnection { implicit c =>
      SQL"update sheets set x = $x, y = $y where id = $id".executeUpdate()
    }

  def setSize(id: Long, width: Double, height: Double) =
    DB.withConnection { implicit c =>
      SQL"update sheets set width = $width, height = $height where id = $id".executeUpdate()
    }

  def setDimension(id: Long, x: Double, y: Double, width: Double, height: Double) =
    DB.withConnection { implicit c =>
      SQL"""update sheets set x = $x, y = $y, width = $width, height = $height
           where id = $id""".executeUpdate()
    }

  def setText(id: Long, text: String) = DB.withConnection { implicit c =>
    SQL"update sheets set content = $text where id = $id".executeUpdate()
  }
}
