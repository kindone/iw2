package models

/**
 * Created by kindone on 2016. 12. 3..
 */
class LogManager {
  lazy val sheetLogManager = new SheetLogManager
  lazy val wallLogManager = new WallLogManager

  def findSheetLog(sheetId: Long)(implicit userId: Long)
    = sheetLogManager.find(sheetId)(userId)
  def createSheetLog(sheetLog: SheetLog)(implicit userId: Long): LogCreationResult
    = sheetLogManager.create(sheetLog)(userId)

  def findWallLog(wallId: Long)(implicit userId: Long)
  = wallLogManager.find(wallId)(userId)
  def createWallLog(wallLog: WallLog)(implicit userId: Long): LogCreationResult
  = wallLogManager.create(wallLog)(userId)


}
