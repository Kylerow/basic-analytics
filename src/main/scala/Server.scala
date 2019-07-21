import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
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
            eventStorage.saveEvent(Event(timestamp.toLong,user.toLong, EventType.withName(event)))
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, ""))
        }
      }
    } ~
    path("admin" / "clear-data"){
      put{
        statisticsStorage.clear()
        EventPersistence.clear()
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, ""))
      }
    } ~
    path("admin" / "hour-plus-one"){
      put{
        AnalyticsTiming.setHourToPresentPlusOne()
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, ""))
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