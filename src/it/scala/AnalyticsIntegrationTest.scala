import akka.http.scaladsl.Http.ServerBinding
import org.apache.http.HttpResponse
import org.apache.http.client.methods.{HttpGet, HttpPost, HttpPut, HttpUriRequest}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.Future

object AnalyticsIntegrationTest{
  val embeddedServer =
    System.getProperty("analytics.server.embedded") match {
      case booleanString: String => booleanString.toBoolean
      case _ => false
    }
  var binding: Future[ServerBinding] = null;
  def start() = if(embeddedServer) binding = ServerStarter.asyncStart
  def stop() =  if(embeddedServer) ServerStarter.asyncStop(binding)
}

trait AnalyticsIntegrationTest
  extends FlatSpec with Matchers with BeforeAndAfterAll {

  override def beforeAll() =
    AnalyticsIntegrationTest.start()

  override def afterAll() =
    AnalyticsIntegrationTest.stop()

  def postUri(timestamp: Long, user: String, event: String) =
    new HttpPost( s"http://localhost:8080/analytics?" +
      s"timestamp=${timestamp}&" +
      s"user=${user}&" +
      s"event=${event}")

  def getUri(timestamp: Long): HttpUriRequest =
    new HttpGet(s"http://localhost:8080/analytics?" +
      s"timestamp=${timestamp}")

  def clearDataUri: HttpUriRequest =
    new HttpPut("http://localhost:8080/admin/clear-data")

  def hourPlusOneUri: HttpUriRequest =
    new HttpPut("http://localhost:8080/admin/hour-plus-one")

  def result (httpResponses: HttpResponse*) :List[String] = {
    httpResponses.map {
      httpResponse =>
        val content = httpResponse.getEntity.getContent
        val bytes = Array.ofDim[Byte](content.available())
        content.read(bytes)
        new String(bytes)
    }.toList
  }
}
