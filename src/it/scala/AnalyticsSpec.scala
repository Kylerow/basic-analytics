import org.apache.http.HttpResponse
import org.apache.http.client.methods.{HttpGet, HttpPost, HttpPut, HttpUriRequest}
import org.apache.http.impl.client.HttpClients
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class AnalyticsSpec extends FlatSpec with Matchers{
  
  "An event" should "increment unique users from 0 to 1" in {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearCacheUri)

    val timestamp = 1
    val user = "5"
    val event = "click"

    val hour = AnalyticsTiming.getHour
    httpclient.execute(postUri(timestamp,user,event))
    val resultValue = result(httpclient.execute(getUri(timestamp)))(0)

    hour shouldBe AnalyticsTiming.getHour
    resultValue.split('\n')(0).split(',')(1) shouldBe "1"
    httpclient.close()
  }
  it should "create increment to two unique users if one exists" in  {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearCacheUri)

    val timestamp = 1
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
//  it should "not create a unique user for the current hour if it's outside the current hour" in {
//    1 shouldBe 1
//  }
  it should "not increment unique user, if the user is not unique" in  {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearCacheUri)

    val timestamp = 1
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

    val timestamp = 1
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

    val timestamp = 1
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

  /// TODO validate return codes

  /// TODO only get stats for current hour

  /// TODO after current hour, start with long term - which don't disappear

  /// TODO validation for POST
  ///       * error for unknown event

  /// TODO validation for GET

  /// TODO unique users,clicks,impressions go from short term to long term

  def postUri(timestamp: Int, user: String, event: String) =
    new HttpPost( s"http://localhost:8080/analytics?" +
      s"timestamp=${timestamp}&" +
      s"user=${user}&" +
      s"event=${event}")

  def getUri(timestamp: Int): HttpUriRequest =
    new HttpGet(s"http://localhost:8080/analytics?" +
      s"timestamp=${timestamp}")

  def clearCacheUri: HttpUriRequest =
    new HttpPut("http://localhost:8080/clear-cache")

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
