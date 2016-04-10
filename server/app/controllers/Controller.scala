package controllers

import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by kindone on 2016. 3. 27..
 */

class Controller extends play.api.mvc.Controller {

  val LOGGED_IN_AS = "LOGGED_IN_AS"
  val JSON_TYPE = "application/json"

  class UserRequest[A](val userId: Long, request: Request[A]) extends WrappedRequest[A](request)

  object UserAction extends ActionBuilder[UserRequest] {
    def invokeBlock[A](request: Request[A], block: (UserRequest[A]) => Future[Result]) = {
      request.session.get(LOGGED_IN_AS) match {
        case Some(id) =>
          block(new UserRequest(id.toLong, request))
        case _ =>
          Future { Unauthorized }
      }
    }
  }

  object EmptyUserAction extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      request.session.get(LOGGED_IN_AS) match {
        case Some(_) =>
          Future { BadRequest }
        case None =>
          block(request)
      }
    }
  }

  object UnauthorizedAction extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
      Future(Unauthorized)
    }
  }

  object AdminAction extends ActionBuilder[UserRequest] {

    def invokeBlock[A](request: Request[A], block: (UserRequest[A]) => Future[Result]) = {
      request.session.get(LOGGED_IN_AS) match {
        case Some("0") =>
          block(new UserRequest(0, request))
        case _ =>
          Future { Unauthorized }
      }
    }
  }

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
