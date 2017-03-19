package models

import play.api.Play.current
import anorm.Row
import anorm.SqlParser.scalar
import com.kindone.infinitewall.data.{ Sheet, Wall }
import play.api.db.DB
import anorm._

/**
 * Created by kindone on 2016. 3. 27..
 */
class SheetManager {
  val parser = RowParser[Sheet] {
    case Row(id: Long, stateId: Long, x: Double, y: Double,
      width: Double, height: Double, content: java.sql.Clob) =>
      Success(Sheet(id, stateId, x, y, width, height, content.getSubString(1, content.length.asInstanceOf[Int])))
    case row => Error(TypeDoesNotMatch(s"unexpected: $row"))
  }

  def find(id: Long)(implicit userId: Long) = DB.withConnection { implicit c =>
    SQL"""select sheets.id, sheets.state_id,
          sheets.x, sheets.y, sheets.width, sheets.height, sheets.content
          from sheets, sheets_in_wall, walls_of_user
          where sheets_in_wall.sheet_id = $id
          and walls_of_user.user_id = $userId
          and sheets_in_wall.wall_id = walls_of_user.wall_id
          and walls_of_user.wall_id = sheets_in_wall.wall_id
          and sheets.id = $id""".as(parser.singleOpt)
  }

  def delete(id: Long)(implicit userId: Long): Boolean =
    sheetOfUser(id, userId).map { _ =>
      DB.withTransaction { implicit conn =>
        val numSheets = SQL"delete from sheets where id = $id".executeUpdate()
        val numSheetsInWall = SQL"delete from sheets_in_wall where sheet_id = $id".executeUpdate()
        if (numSheets == 1 && numSheetsInWall == 1)
          true
        else
          throw new RuntimeException("failed due to inconsistent deleted records: numSheets=" + numSheets + ", numSheetsInWall=" + numSheetsInWall)
      }
    }.getOrElse(false)

  def setPosition(id: Long, x: Double, y: Double)(implicit userId: Long): Boolean =
    sheetOfUser(id, userId).map { _ =>
      DB.withConnection { implicit c =>
        SQL"update sheets set x = $x, y = $y where id = $id".executeUpdate() == 1
      }
    }.getOrElse(false)

  def setSize(id: Long, width: Double, height: Double)(implicit userId: Long): Boolean =
    sheetOfUser(id, userId).map { _ =>
      DB.withConnection { implicit c =>
        SQL"update sheets set width = $width, height = $height where id = $id".executeUpdate() == 1
      }
    }.getOrElse(false)

  def setDimension(id: Long, x: Double, y: Double, width: Double, height: Double)(implicit userId: Long) =
    sheetOfUser(id, userId).map { _ =>
      DB.withConnection { implicit c =>
        SQL"""update sheets set x = $x, y = $y, width = $width, height = $height
           where id = $id""".executeUpdate() == 1
      }
    }.getOrElse(false)

  def setText(id: Long, text: String)(implicit userId: Long) =
    sheetOfUser(id, userId).map { _ =>
      DB.withConnection { implicit c =>
        SQL"update sheets set content = $text where id = $id".executeUpdate() == 1
      }
    }.getOrElse(false)

  def updateText(id: Long, content: String, from: Int, numDeleted: Int)(implicit userId: Long) = {
    sheetOfUser(id, userId).map { _ =>
      DB.withConnection { implicit c =>
        SQL("update sheets set content = CONCAT(substring(content FROM 0 FOR {from}), {content}, substring(content FROM {upto})) where id = {id}").
          on('content -> content, 'from -> from, 'upto -> (from + numDeleted + 1), 'id -> id).executeUpdate() == 1
      }
    }.getOrElse(false)
  }

  private def sheetOfUser(sheetId: Long, userId: Long): Option[Long] = DB.withConnection { implicit c =>
    SQL"""select sheets_in_wall.wall_id from sheets_in_wall,walls_of_user
        where sheets_in_wall.sheet_id = $sheetId
        and walls_of_user.user_id = $userId
        and walls_of_user.wall_id = sheets_in_wall.wall_id""".map {
      case Row(id: Long) => id
    }.as(scalar[Long].singleOpt)
  }
}
