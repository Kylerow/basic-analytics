import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{HttpApp, Route}

class Server extends HttpApp{
  override def routes: Route =
    path("analytics") {
      get {
        parameter("timestamp".as[String]){
          timestamp =>
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, getStatisticsForTimestamp(timestamp.toLong)))
        }
      } ~
      post {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Hello, World."))
      }
    }
}

object ServerStarter{
  def main(args :Array[String])= (new Server()).startServer("0.0.0.0", 8080)
}