package models

import play.api.Logger
import play.api.Play.current
import anorm.Row
import com.kindone.infinitewall.data.{ Sheet, Wall }
import play.api.db.DB
import anorm._

/**
 * Created by kindone on 2016. 6. 26..
 */
case class SheetLog(sheetId: Long, logId: Long, actionType: Int, action: Option[String])

class SheetLogManager {
  def find(sheetId: Long)(implicit userId: Long) = DB.withConnection { implicit c =>
    SQL"""select log.sheet_id,log.log_id, log.action_type, log.action
          from sheet_logs as log, sheets_in_wall, walls_of_user
          where sheets_in_wall.sheet_id = $sheetId
          and walls_of_user.user_id = $userId
          and sheets_in_wall.wall_id = walls_of_user.wall_id
          and walls_of_user.wall_id = sheets_in_wall.wall_id
          and log.sheet_id = $sheetId""".map {
      case Row(sheetId: Long, logId: Long,
        actionType: Int, action: Option[String]) => SheetLog(sheetId, logId, actionType, action)
    }.list
  }

  def create(sheetLog: SheetLog)(implicit userId: Long): Long = {
    // returns id

    DB.withTransaction { implicit c =>
      val maxId =
        SQL"""select COALESCE(MAX(log_id),0) from sheet_logs
             where sheet_id = ${sheetLog.sheetId}""".map {
          case Row(id: Long) => id
        }.single()

      SQL"""insert into sheet_logs(sheet_id, log_id, action_type, action)
           VALUES(${sheetLog.sheetId}, ${maxId}+1, ${sheetLog.actionType}, ${sheetLog.action})
        """.executeInsert()

      maxId
    }
    //    Logger.info(find(sheetLog.sheetId).toString)
  }

}
