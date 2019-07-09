import org.apache.http.HttpResponse
import org.apache.http.client.methods.{HttpGet, HttpPost, HttpUriRequest}
import org.apache.http.impl.client.HttpClients
import org.scalatest.{FlatSpec, Matchers}

class AnalyticsSpec extends FlatSpec with Matchers {
  val httpclient = HttpClients.createDefault

  "An event" should "be created a unique user if none exists" in {
    val timestamp = 1
    val user = "bob"
    val event = "click"

    val hour = AnalyticsTiming.getHour
    httpclient.execute(postUri(timestamp,user,event))
    val resultValue = result(httpclient.execute(getUri(timestamp)))(0)

    hour shouldBe AnalyticsTiming.getHour
    resultValue.split('\n')(0).split(',')(1) shouldBe "1"
  }
  it should "not create a unique user for the current hour if it's outside the current hour" in {
    1 shouldBe 1
  }
  "A unique user for the current hour" should "disappear when the hour changes" in {
    1 shouldBe 1
  }

  /// TODO continue with current hour - for clicks and impressions

  /// TODO after current hour, start with long term - which don't disappear

  /// TODO validation for POST

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
