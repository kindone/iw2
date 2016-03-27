package controllers

import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.iteratee._
import play.api.libs.json.JsValue
import play.api.libs.json.Json

/**
 * Created by kindone on 2016. 3. 27..
 */
class Controller extends play.api.mvc.Controller {
  def bodyText(implicit request: Request[AnyContent]) = request.body.asText.get

  def jsonParams(implicit request: Request[AnyContent]) = request.body.asJson.get

  def jsonParam(key: String)(implicit request: Request[AnyContent]) = (jsonParams \ key).as[String]

  // params by query (usually used in GET method)
  def queryParams(implicit header: RequestHeader) = header.queryString

  def queryParam(key: String)(implicit header: RequestHeader) = queryParams(header).get(key).get.head

  // form encoding (usually used in POST method)
  def formParams(implicit request: Request[AnyContent]) = request.body.asFormUrlEncoded.get

  def formParam(key: String)(implicit request: Request[AnyContent]) = formParams.get(key).get.head
}
