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
  def find(id: Long)(implicit userId: Long) = DB.withConnection { implicit c =>
    SQL"""select sheets.id, sheets.x, sheets.y, sheets.width, sheets.height, sheets.content
          from sheets, sheets_in_wall, walls_of_user
          where sheets_in_wall.sheet_id = $id
          and walls_of_user.user_id = $userId
          and sheets_in_wall.wall_id = walls_of_user.wall_id
          and walls_of_user.wall_id = sheets_in_wall.wall_id
          and sheets.id = $id""".map {
      case Row(id: Long, x: Double, y: Double,
        width: Double, height: Double, content: java.sql.Clob) => Sheet(id, x, y, width, height, content.getSubString(1, content.length.asInstanceOf[Int]))
    }.singleOpt
  }

  def delete(id: Long)(implicit userId: Long) =
    sheetOfUser(id, userId).foreach { _ =>
      DB.withTransaction { implicit c =>
        for (sheet <- find(id)) {
          SQL"delete from sheets where id = $id".executeUpdate()
          SQL"delete from sheets_in_wall where sheet_id = $id".executeUpdate()

        }
      }
    }

  def setPosition(id: Long, x: Double, y: Double)(implicit userId: Long) =
    sheetOfUser(id, userId).foreach { _ =>
      DB.withConnection { implicit c =>
        SQL"update sheets set x = $x, y = $y where id = $id".executeUpdate()
      }
    }

  def setSize(id: Long, width: Double, height: Double)(implicit userId: Long) =
    sheetOfUser(id, userId).foreach { _ =>
      DB.withConnection { implicit c =>
        SQL"update sheets set width = $width, height = $height where id = $id".executeUpdate()
      }
    }

  def setDimension(id: Long, x: Double, y: Double, width: Double, height: Double)(implicit userId: Long) =
    sheetOfUser(id, userId).foreach { _ =>
      DB.withConnection { implicit c =>
        SQL"""update sheets set x = $x, y = $y, width = $width, height = $height
           where id = $id""".executeUpdate()
      }
    }

  def setText(id: Long, text: String)(implicit userId: Long) =
    sheetOfUser(id, userId).foreach { _ =>
      DB.withConnection { implicit c =>
        SQL"update sheets set content = $text where id = $id".executeUpdate()
      }
    }

  private def sheetOfUser(sheetId: Long, userId: Long): Option[Long] = DB.withConnection { implicit c =>
    SQL"""select sheets_in_wall.wall_id from sheets_in_wall,walls_of_user
        where sheets_in_wall.sheet_id = $sheetId
        and walls_of_user.user_id = $userId
        and walls_of_user.wall_id = sheets_in_wall.wall_id""".map {
      case Row(id: Long) => id
    }.singleOpt
  }
}
