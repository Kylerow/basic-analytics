import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}

class AnalyticsTimingSpec extends FlatSpec with Matchers {
  "isCurrentHour" should "return true for a timestamp in the current hour" in {
    AnalyticsTiming.isCurrentHour(DateTime.now().getMillis) shouldBe true
  }
  "isCurrentHour" should "return false for a timestamp in past hour" in {
    AnalyticsTiming.isCurrentHour(DateTime.now().getMillis-(1000*60*60)) shouldBe false
  }
}
