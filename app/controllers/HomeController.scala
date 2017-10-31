package controllers

import javax.inject._


import models.ConversationData
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._
import play.api.mvc._
import services.WatsonConversation



/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, ws: WatsonConversation)
  extends AbstractController(cc) with I18nSupport {

  val conversationForm = Form(
    mapping(
      "payload" -> nonEmptyText
    )(ConversationData.apply)(ConversationData.unapply)
  )


  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def chatbot() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.latambot(conversationForm))
  }


  def conversation() = Action { implicit request: Request[AnyContent] =>

    conversationForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.latambot(conversationForm))
      },
      cd => {
        val watsonResponse = ws.getResponseFromWatson(cd.payload)
        println(s"watsonResponse : $watsonResponse")
        Redirect(routes.HomeController.conversation).flashing("Watson Response" -> watsonResponse)
      }
    )

  }


}
