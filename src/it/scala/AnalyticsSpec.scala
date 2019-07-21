import org.apache.http.HttpResponse
import akka.http.scaladsl.Http.ServerBinding
import org.apache.http.client.methods.{HttpGet, HttpPost, HttpPut, HttpUriRequest}
import org.apache.http.impl.client.HttpClients
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.Future

class AnalyticsSpec extends FlatSpec with Matchers with BeforeAndAfterAll {
  val embeddedServer =
    System.getProperty("analytics.server.embedded") match {
      case booleanString: String => booleanString.toBoolean
      case _ => false
    }
  var binding: Future[ServerBinding] = null;

  override def beforeAll() =
    if(embeddedServer) binding = ServerStarter.asyncStart

  override def afterAll() =
    if (embeddedServer) ServerStarter.asyncStop(binding)

  "An event" should "increment unique users from 0 to 1" in {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearCacheUri)

    val timestamp = DateTime.now(DateTimeZone.UTC).getMillis
    val user = "5"
    val event = "click"

    val hour = AnalyticsTiming.getHour
    httpclient.execute(postUri(timestamp,user,event))
    val resultValue = result(httpclient.execute(getUri(timestamp)))(0)

    hour shouldBe AnalyticsTiming.getHour
    resultValue.split('\n')(0).split(',')(1) shouldBe "1"
    httpclient.close()
  }
  it should "increment to two unique users if one exists" in  {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearCacheUri)

    val timestamp = DateTime.now(DateTimeZone.UTC).getMillis
    val user = "5"
    val user2 = "72"
    val event = "click"

    val hour = AnalyticsTiming.getHour
    httpclient.execute(postUri(timestamp,user,event))
    httpclient.execute(postUri(timestamp,user2,event))
    val resultValue = result(httpclient.execute(getUri(timestamp)))(0)

    hour shouldBe AnalyticsTiming.getHour
    resultValue.split('\n')(0).split(',')(1) shouldBe "2"
    httpclient.close()
  }

  it should "not increment unique user, if the user is not unique" in  {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearCacheUri)

    val timestamp = DateTime.now(DateTimeZone.UTC).getMillis
    val user = "5"
    val user2 = "5"
    val event = "click"

    val hour = AnalyticsTiming.getHour
    httpclient.execute(postUri(timestamp,user,event))
    httpclient.execute(postUri(timestamp,user2,event))
    val resultValue = result(httpclient.execute(getUri(timestamp)))(0)

    hour shouldBe AnalyticsTiming.getHour
    resultValue.split('\n')(0).split(',')(1) shouldBe "1"
    httpclient.close()

  }
  it should "increment clicks" in {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearCacheUri)

    val timestamp = DateTime.now(DateTimeZone.UTC).getMillis
    val user = "5"
    val event = "click"

    val hour = AnalyticsTiming.getHour
    httpclient.execute(postUri(timestamp,user,event))
    httpclient.execute(postUri(timestamp+1,user,event))
    val resultValue = result(httpclient.execute(getUri(timestamp)))(0)

    hour shouldBe AnalyticsTiming.getHour
    resultValue.split('\n')(1).split(',')(1) shouldBe "2"
    httpclient.close()
  }
  it should "increment impressions" in {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearCacheUri)

    val timestamp = DateTime.now(DateTimeZone.UTC).getMillis
    val user = "5"
    val event = "impression"

    val hour = AnalyticsTiming.getHour
    httpclient.execute(postUri(timestamp,user,event))
    httpclient.execute(postUri(timestamp+1,user,event))
    val resultValue = result(httpclient.execute(getUri(timestamp)))(0)

    hour shouldBe AnalyticsTiming.getHour
    resultValue.split('\n')(2).split(',')(1) shouldBe "2"
    httpclient.close()
  }

  it should "not increment the current hour stat, if entered for previous hour" in {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearCacheUri)

    val timestamp = (DateTime.now(DateTimeZone.UTC).getMillis) - (1000*60*60)
    val currentTimestamp = DateTime.now(DateTimeZone.UTC).getMillis
    val user = "5"
    val event = "impression"

    val hour = AnalyticsTiming.getHour
    httpclient.execute(postUri(timestamp,user,event))
    httpclient.execute(postUri(timestamp+1,user,event))
    val resultValue = result(httpclient.execute(getUri(currentTimestamp)))(0)

    hour shouldBe AnalyticsTiming.getHour
    resultValue.split('\n')(2).split(',')(1) shouldBe "0"
    httpclient.close()
  }

//  it should "increment the previous hour stat, if entered for previous hour" in {
//    val httpclient = HttpClients.createDefault
//    httpclient.execute(clearCacheUri)
//
//    val timestamp = (DateTime.now(DateTimeZone.UTC).getMillis) - (1000*60*60)
//    val user = "5"
//    val event = "impression"
//
//    val hour = AnalyticsTiming.getHour
//    httpclient.execute(postUri(timestamp,user,event))
//    httpclient.execute(postUri(timestamp+1,user,event))
//    val resultValue = result(httpclient.execute(getUri(timestamp)))(0)
//
//    hour shouldBe AnalyticsTiming.getHour
//    resultValue.split('\n')(2).split(',')(1) shouldBe "2"
//    httpclient.close()
//  }

  it should "not affect the current statistics once the hour changes" in {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearCacheUri)

    val timestamp = DateTime.now(DateTimeZone.UTC).getMillis
    val user = "5"
    val event = "click"

    val hour = AnalyticsTiming.getHour
    httpclient.execute(postUri(timestamp,user,event))
    val resultValue = result(httpclient.execute(getUri(timestamp)))(0)
    hour shouldBe AnalyticsTiming.getHour
    resultValue.split('\n')(0).split(',')(1) shouldBe "1"

    httpclient.execute(hourPlusOneUri)

    val resultValue2 = result(httpclient.execute(getUri(timestamp)))(0)
    resultValue2.split('\n')(0).split(',')(1) shouldBe "0"
    httpclient.close()
  }

  /// TODO validation for POST
  ///       * error for unknown event

  /// TODO validation for GET
  ///       * correct data types
  ///       * only checking for this hour

  /// TODO validate return codes

  /// TODO unique users,clicks,impressions go from short term to long term

  /// TODO multithread / scale

  def postUri(timestamp: Long, user: String, event: String) =
    new HttpPost( s"http://localhost:8080/analytics?" +
      s"timestamp=${timestamp}&" +
      s"user=${user}&" +
      s"event=${event}")

  def getUri(timestamp: Long): HttpUriRequest =
    new HttpGet(s"http://localhost:8080/analytics?" +
      s"timestamp=${timestamp}")

  def clearCacheUri: HttpUriRequest =
    new HttpPut("http://localhost:8080/admin/clear-cache")

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
