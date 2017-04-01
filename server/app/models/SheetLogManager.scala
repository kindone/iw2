package models

import anorm.SqlParser._
import com.kindone.infinitewall.data.state.Wall
import play.api.Logger
import play.api.Play.current
import anorm.Row
import play.api.db.DB
import anorm._

/**
 * Created by kindone on 2016. 6. 26..
 */
case class SheetLog(sheetId: Long, logId: Long, actionType: Int, action: Option[String])
case class LogCreationResult(logId: Long, success: Boolean)
case class LogCreationResultWithId(logId: Long, success: Boolean, id: Long)

class SheetLogManager {

  val parser = RowParser[SheetLog] {
    case Row(sheetId: Long, logId: Long,
      actionType: Int, action: Option[String]) => Success(SheetLog(sheetId, logId, actionType, action))
    case row => Error(TypeDoesNotMatch(s"unexpected: $row"))
  }

  def find(sheetId: Long)(implicit userId: Long) = DB.withConnection { implicit c =>
    SQL"""select log.sheet_id,log.log_id, log.action_type, log.action
          from sheet_logs as log, sheets_in_wall, walls_of_user
          where sheets_in_wall.sheet_id = $sheetId
          and walls_of_user.user_id = $userId
          and sheets_in_wall.wall_id = walls_of_user.wall_id
          and log.sheet_id = $sheetId""".map {
      case Row(sheetId: Long, logId: Long,
        actionType: Int, action: Option[String]) => SheetLog(sheetId, logId, actionType, action)
    }.as(parser.*)
  }

  def create(sheetLog: SheetLog)(implicit userId: Long): LogCreationResult = {
    // returns id

    DB.withTransaction { implicit c =>
      val sheetId = sheetOfUser(sheetLog.sheetId, userId).get // will throw error if not exists

      val maxId =
        SQL"""select COALESCE(MAX(log_id),0) from sheet_logs
             where sheet_id = ${sheetId}""".map {
          case Row(id: Long) => id
        }.as(scalar[Long].single)

      if (maxId == sheetLog.logId) {
        SQL"""insert into sheet_logs(sheet_id, log_id, action_type, action)
           VALUES(${sheetId}, ${maxId}+1, ${sheetLog.actionType}, ${sheetLog.action})
        """.executeInsert()

        LogCreationResult(maxId + 1, true)
      } else
        LogCreationResult(maxId, false)
    }
  }

  private def sheetOfUser(sheetId: Long, userId: Long): Option[Long] = DB.withConnection { implicit c =>
    SQL"""select sheets_in_wall.sheet_id from sheets_in_wall, walls_of_user
         where sheets_in_wall.sheet_id = $sheetId
         and walls_of_user.user_id = $userId
         and sheets_in_wall.wall_id = walls_of_user.wall_id
         """.as(scalar[Long].singleOpt)
  }

}
