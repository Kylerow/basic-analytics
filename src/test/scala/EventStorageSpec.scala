import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}
import org.mockito.Mockito._

class EventStorageSpec  extends FlatSpec with Matchers {
  "event storage" should "return a statistic with one unique user" in {
    val eventStorage = new EventStorage
    val statistics = new Statistics
    eventStorage.saveEvent(Event(DateTime.now().getMillis, 42, EventType.CLICK))

    val result = statistics.getStatistic(DateTime.now())
    result.uniqueUsers.get shouldBe 1
  }
  "save event" should "update cache and push to persistent storage" in {
    val mockStatistics = mock(classOf[Statistics])
    val mockEventPersistence = mock(classOf[EventPersistence])
    val eventStorage = new EventStorage{
      _statistics = mockStatistics
      _eventPersistence = mockEventPersistence
    }
    val event = Event(1,1,EventType.CLICK)

    eventStorage.saveEvent(event)

    verify(mockStatistics).maintainCacheAlignment()
    verify(mockStatistics).updateStatisticsCache(event)
    verify(mockEventPersistence).save(event)
  }
}
