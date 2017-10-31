package services

import java.util.concurrent.TimeUnit
import javax.inject._

import play.api.libs.ws._

import scala.concurrent.{Await, ExecutionContext, Future}
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService
import com.ibm.watson.developer_cloud.conversation.v1.model.{MessageRequest, MessageResponse}

import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

@Singleton
class WatsonConversation @Inject() ( implicit ec: ExecutionContext,ws: WSClient, configuration: play.api.Configuration) {

  val url= configuration.underlying.getString("watsonConversation.url")
  val username= configuration.underlying.getString("watsonConversation.username")
  val password=configuration.underlying.getString("watsonConversation.password")
  val workspace=configuration.underlying.getString("watsonConversation.workspace")

  private val cs : ConversationService = new ConversationService("2016-07-11")
  cs.setUsernameAndPassword(username, password)


  def watsonService(payload: String): Future[String] = Future {
      val request = new MessageRequest.Builder().inputText(payload).build()
      val watsonResponse: MessageResponse = cs.message(workspace, request).execute()
      val watsonOutput = watsonResponse.getOutput().toString()
      watsonOutput
  }

  def getResponseFromWatson(payload: String) : String = {

    val watsonOutput: Future[String] = watsonService(payload)
    watsonOutput foreach { output =>
      println(s"watsonOutput: $output")
    }

    // if watsonOutput should be enriched => call enrichWatsonOutput with TaxCalculation
    println(s"llamando a Python:")
        import play.api.libs.ws._
        val rqst: WSRequest = ws.url("https://python-latambot-20171028154841318.mybluemix.net")
          .addHttpHeaders("Accept" -> "application/json")
          .addHttpHeaders("Content-Type" -> "application/json")

        val futureResponse: Future[String] = rqst.get().map( r => r.body)
        val response = Await.result(futureResponse,Duration.apply(10,TimeUnit.SECONDS))
    println(s"respuesta de Python: $response")

    response
  }


  // post to python (in progress)
  def getResponseFromPython(data: String) : Future[String] = {
    import play.api.libs.json._
    import play.api.libs.ws._

    val pythonService = "python-latambot-20171028154841318.mybluemix.net"

    val request: WSRequest = ws.url(pythonService)
      .addHttpHeaders("Accept" -> "application/json")
      .addHttpHeaders("Content-Type" -> "application/json")

    val futureResponse: Future[String] = request
      .post(data)
      .map {
        response =>
          println(response.json.toString)
          (response.json \ "conversations" \\ "conversation").map( xml => xml.as[String]).head
      }

    futureResponse
  }

}



