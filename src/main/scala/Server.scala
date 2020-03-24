import java.util.NoSuchElementException
//this is a test
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.ActorMaterializer

import scala.concurrent.Future

class Server extends HttpApp with Dependencies{
  override def routes: Route =
    path("analytics") {
      get {
        parameter("timestamp".as[String]){
          timestamp =>
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, getStatisticsForTimestamp(timestamp.toLong)))
        }
      } ~
      post {
        parameters('timestamp.as[String], 'user.as[String], 'event.as[String]) {
        (timestamp, user, event) =>
          try {
            val eventType = EventType.withName(event)
            eventStorage.saveEvent(Event(timestamp.toLong,user.toLong, eventType))
            complete(StatusCodes.NoContent)
          } catch {
            case _ :NoSuchElementException => complete(StatusCodes.BadRequest,"Invalid Event Type")
            case _ :Throwable => complete(StatusCodes.InternalServerError ,"Server Error")
          }
        }
      }
    } ~
    path("admin" / "clear-data"){
      put{
        statisticsStorage.clear()
        EventPersistence.clear()
        complete(StatusCodes.NoContent)
      }
    } ~
    path("admin" / "hour-plus-one"){
      put{
        AnalyticsTiming.setHourToPresentPlusOne()
        complete(StatusCodes.NoContent)
      }
    }
}

object ServerStarter{
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  var asyncBinding :Future[ServerBinding] = null

  def main(args :Array[String]) :Unit = syncStart

  def syncStart = (new Server()).startServer("0.0.0.0", 8080)

  def asyncStart = Http().bindAndHandle(new Server().routes, "localhost", 8080)

  def asyncStop(bindingFuture: Future[ServerBinding]) = bindingFuture.flatMap(_.unbind())

}
