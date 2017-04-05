package models

import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.state.{ Wall, Sheet }
import upickle.default._
import play.api.db.DB
import play.api.Play.current

/**
 * Created by kindone on 2016. 12. 3..
 */

object ModelManager {
  val MOVE_SHEET = 0
  val RESIZE_SHEET = 1
  val CHANGE_SHEET_DIMENSION = 2
  val CHANGE_SHEET_CONTENT = 3
  val CHANGE_WALL_PAN = 4
  val CHANGE_WALL_ZOOM = 5
  val CHANGE_WALL_VIEW = 6
  val CHANGE_WALL_TITLE = 7
  val CREATE_SHEET = 8
  val DELETE_SHEET = 9
  val CREATE_WALL = 10
  val DELETE_WALL = 11
}

class ModelManager {
  import ModelManager._
  lazy val sheetManager = new SheetManager
  lazy val wallManager = new WallManager
  lazy val logManager = new LogManager

  private def withTransaction[A](expr: => A): A = DB.withConnection { implicit conn =>
    expr
  }

  def findSheet(id: Long)(implicit userId: Long): Option[Sheet] = sheetManager.find(id)(userId)

  def deleteSheet(id: Long)(implicit userId: Long): Unit = sheetManager.delete(id)(userId)

  def moveSheet(stateId: Long, action: MoveSheetAction)(implicit userId: Long): LogCreationResult = withTransaction[LogCreationResult] {
    val result = logManager.createSheetLog(SheetLog(action.sheetId, stateId, MOVE_SHEET, Some(write(action))))(userId)
    if (result.success)
      sheetManager.setPosition(action.sheetId, action.x, action.y)(userId)
    result
  }

  def resizeSheet(stateId: Long, action: ResizeSheetAction)(implicit userId: Long): LogCreationResult = withTransaction {
    val result = logManager.createSheetLog(SheetLog(action.sheetId, stateId, RESIZE_SHEET, Some(write(action))))(userId)
    if (result.success)
      sheetManager.setSize(action.sheetId, action.width, action.height)(userId)
    result
  }

  def setSheetDimension(stateId: Long, action: ChangeSheetDimensionAction)(implicit userId: Long): LogCreationResult = withTransaction {
    val result = logManager.createSheetLog(SheetLog(action.sheetId, stateId, CHANGE_SHEET_DIMENSION, Some(write(action))))(userId)
    if (result.success)
      sheetManager.setSize(action.sheetId, action.width, action.height)(userId)
    result
  }

  def setSheetText(stateId: Long, action: ChangeSheetContentAction)(implicit userId: Long): LogCreationResult = withTransaction {
    val result = logManager.createSheetLog(SheetLog(action.sheetId, stateId, CHANGE_SHEET_CONTENT, Some(write(action))))(userId)
    if (result.success)
      sheetManager.updateText(action.sheetId, action.content, action.from, action.numDeleted)(userId)
    result
  }

  def findAllWalls()(implicit userId: Long): List[Wall] = wallManager.findAll()(userId)

  def findWall(id: Long)(implicit userId: Long): Option[Wall] = wallManager.find(id)(userId)

  def createWall(stateId: Long, action: CreateWallAction)(implicit userId: Long): LogCreationResultWithId = withTransaction {
    val newWallId = wallManager.create(Wall(0, 0, action.x, action.y, action.scale, action.title))(userId).get
    val result = logManager.createWallLog(WallLog(newWallId, stateId, CREATE_WALL, Some(write(action))))(userId)
    LogCreationResultWithId(result.logId, result.success, newWallId)
  }

  def deleteWall(stateId: Long, action: DeleteWallAction)(implicit userId: Long): LogCreationResult = withTransaction {
    val result = logManager.createWallLog(WallLog(action.wallId, stateId, DELETE_WALL, Some(write(action))))(userId)
    wallManager.delete(action.wallId)(userId)
    result
  }
  def setWallPan(stateId: Long, action: ChangePanAction)(implicit userId: Long): LogCreationResult = withTransaction {
    val result = logManager.createWallLog(WallLog(action.wallId, stateId, CHANGE_WALL_PAN, Some(write(action))))(userId)
    if (result.success)
      wallManager.setPan(action.wallId, action.x, action.y)(userId)
    result
  }

  def setWallZoom(stateId: Long, action: ChangeZoomAction)(implicit userId: Long): LogCreationResult = withTransaction {
    val result = logManager.createWallLog(WallLog(action.wallId, stateId, CHANGE_WALL_ZOOM, Some(write(action))))(userId)
    if (result.success)
      wallManager.setZoom(action.wallId, action.scale)(userId)
    result
  }

  def setWallView(stateId: Long, action: ChangeViewAction)(implicit userId: Long): LogCreationResult = withTransaction {
    val result = logManager.createWallLog(WallLog(action.wallId, stateId, CHANGE_WALL_VIEW, Some(write(action))))(userId)
    if (result.success)
      wallManager.setView(action.wallId, action.x, action.y, action.scale)(userId)
    result
  }

  def setWallTitle(stateId: Long, action: ChangeTitleAction)(implicit userId: Long): LogCreationResult = withTransaction {
    val result = logManager.createWallLog(WallLog(action.wallId, stateId, CHANGE_WALL_TITLE, Some(write(action))))(userId)
    if (result.success)
      wallManager.setTitle(action.wallId, action.title)(userId)
    result
  }

  def getSheetsInWall(id: Long)(implicit userId: Long): List[Sheet] = wallManager.getSheets(id)(userId)

  def getSheetIdsInWall(id: Long)(implicit userId: Long): Set[Long] = wallManager.getSheetIds(id)(userId)

  def createSheetInWall(stateId: Long, action: CreateSheetAction)(implicit userId: Long): LogCreationResultWithId = withTransaction {
    val newSheetId = wallManager.createSheet(action.wallId, action.sheet)(userId).get
    val newAction = CreateSheetAction(action.wallId, action.sheet.copy(id = newSheetId))
    val result = logManager.createWallLog(WallLog(action.wallId, stateId, CREATE_SHEET, Some(write(newAction))))(userId)
    LogCreationResultWithId(result.logId, result.success, newSheetId)
  }

  def deleteSheetInWall(stateId: Long, action: DeleteSheetAction)(implicit userId: Long): LogCreationResult = withTransaction {
    wallManager.deleteSheet(action.wallId, action.sheetId)(userId)
    logManager.createWallLog(WallLog(action.wallId, stateId, DELETE_SHEET, Some(write(action))))(userId)
  }

}
