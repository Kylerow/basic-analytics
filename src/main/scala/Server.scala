import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{HttpApp, Route}

class Server extends HttpApp{
  val eventStorage = new EventStorage()
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
            eventStorage.saveEvent(Event(timestamp.toLong,user.toLong, EventType(event)))
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, ""))
        }
      }
    }
}

object ServerStarter{
  def main(args :Array[String])= (new Server()).startServer("0.0.0.0", 8080)
}