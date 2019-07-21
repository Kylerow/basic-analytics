import org.apache.http.impl.client.HttpClients
import org.joda.time.{DateTime, DateTimeZone}

class CachedSpec  extends AnalyticsIntegrationTest{
  "An event" should "increment unique users from 0 to 1" in {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearDataUri)

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
    httpclient.execute(clearDataUri)

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
    httpclient.execute(clearDataUri)

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
    httpclient.execute(clearDataUri)

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
    httpclient.execute(clearDataUri)

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
  it should "not affect the current statistics once the hour changes" in {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearDataUri)

    val timestamp = DateTime.now(DateTimeZone.UTC).getMillis
    val user = "5"
    val event = "click"

    val hour = AnalyticsTiming.getHour
    httpclient.execute(postUri(timestamp,user,event))
    val resultValue = result(httpclient.execute(getUri(timestamp)))(0)
    hour shouldBe AnalyticsTiming.getHour
    resultValue.split('\n')(0).split(',')(1) shouldBe "1"

    httpclient.execute(hourPlusOneUri)

    val resultValue2 = result(httpclient.execute(getUri(timestamp+(1000*60*60))))(0)
    resultValue2.split('\n')(0).split(',')(1) shouldBe "0"
    httpclient.close()
  }
}
