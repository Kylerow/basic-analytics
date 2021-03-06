import org.apache.http.impl.client.HttpClients
import org.joda.time.{DateTime, DateTimeZone}


class AnalyticsSpec extends AnalyticsIntegrationTest{
  "An event" should "not increment the current hour stat, if entered for previous hour" in {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearDataUri)

    val timestamp = (DateTime.now(DateTimeZone.UTC).getMillis) - (1000*60*60)
    val currentTimestamp = DateTime.now(DateTimeZone.UTC).getMillis
    val user = "5"
    val event = "impression"

    val hour = AnalyticsTiming.getHour
    val postResult = httpclient.execute(postUri(timestamp,user,event))
    httpclient.execute(postUri(timestamp+1,user,event))
    val resultValue = result(httpclient.execute(getUri(currentTimestamp)))(0)

    hour shouldBe AnalyticsTiming.getHour
    postResult.getStatusLine.getStatusCode shouldBe 204
    resultValue.content.split('\n')(2).split(',')(1) shouldBe "0"
    httpclient.close()
  }

  it should "increment the two hour ago stat (impressions), if entered for two hours ago" in {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearDataUri)

    val timestamp = (DateTime.now(DateTimeZone.UTC).getMillis) - (1000*60*60*2)
    val user = "5"
    val event = "impression"

    val hour = AnalyticsTiming.getHour
    val postResult = httpclient.execute(postUri(timestamp,user,event))
    httpclient.execute(postUri(timestamp+1,user,event))
    val resultValue = result(httpclient.execute(getUri(timestamp)))(0)

    hour shouldBe AnalyticsTiming.getHour
    postResult.getStatusLine.getStatusCode shouldBe 204
    resultValue.content.split('\n')(2).split(',')(1) shouldBe "2"
    httpclient.close()
  }
  it should "increment the two hour ago stat (clicks), if entered for two hours ago" in {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearDataUri)

    val timestamp = (DateTime.now(DateTimeZone.UTC).getMillis) - (1000*60*60*2)
    val user = "5"
    val event = "click"

    val hour = AnalyticsTiming.getHour
    httpclient.execute(postUri(timestamp,user,event))
    httpclient.execute(postUri(timestamp+1,user,event))
    val resultValue = result(httpclient.execute(getUri(timestamp)))(0)

    hour shouldBe AnalyticsTiming.getHour
    resultValue.content.split('\n')(1).split(',')(1) shouldBe "2"
    httpclient.close()
  }
  it should "increment the two hour ago stat (unique users), if entered for two hours ago" in {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearDataUri)

    val timestamp = (DateTime.now(DateTimeZone.UTC).getMillis) - (1000*60*60*2)
    val user = "5"
    val event = "click"

    val hour = AnalyticsTiming.getHour
    httpclient.execute(postUri(timestamp,user,event))
    httpclient.execute(postUri(timestamp+1,user,event))
    val resultValue = result(httpclient.execute(getUri(timestamp)))(0)

    hour shouldBe AnalyticsTiming.getHour
    resultValue.content.split('\n')(0).split(',')(1) shouldBe "1"
    httpclient.close()
  }

  it should "give an error for an unknown event type" in {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearDataUri)

    val timestamp = (DateTime.now(DateTimeZone.UTC).getMillis)
    val user = "5"
    val event = "scroll"

    val hour = AnalyticsTiming.getHour
    val postResult = httpclient.execute(postUri(timestamp,user,event))
    val resultValue = result(httpclient.execute(getUri(timestamp)))(0)

    hour shouldBe AnalyticsTiming.getHour
    postResult.getStatusLine.getStatusCode shouldBe 400
    httpclient.close()
  }
}
