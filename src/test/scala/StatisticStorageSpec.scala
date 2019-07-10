import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}

class StatisticStorageSpec  extends FlatSpec with Matchers {
  "event storage" should "return a statistic with one unique user" in {
    val eventStorage = new EventStorage
    eventStorage.saveEvent(Event(1, 42, EventType.CLICK))

    val result = eventStorage.getStatistic(new DateTime(1))
    result.uniqueUsers.get shouldBe 1
  }
}
