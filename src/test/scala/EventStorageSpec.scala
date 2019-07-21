import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}

class EventStorageSpec  extends FlatSpec with Matchers {
  "event storage" should "return a statistic with one unique user" in {
    val eventStorage = new EventStorage
    val statistics = new Statistics
    eventStorage.saveEvent(Event(DateTime.now().getMillis, 42, EventType.CLICK))

    val result = statistics.getStatistic(DateTime.now())
    result.uniqueUsers.get shouldBe 1
  }
  "save event" should "update cache and push to persistent storage" in {
    // production code:
    // 1) maintainCacheAlignment
    // 2) update statistics cache
    // 3) send to persistent storage

    // steps to glorious success
    // 1) partial mock the three tasks
    // 2) call saveEvent
    // 3) verify that the three methods are called

  }
}
