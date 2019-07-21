import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{FlatSpec, Matchers}
import org.mockito.Mockito._

class EventStorageSpec  extends FlatSpec with Matchers {
  "event storage" should "return a statistic with one unique user" in {
    val eventStorage = new EventStorage
    val statistics = new Statistics
    eventStorage.saveEvent(Event(DateTime.now(DateTimeZone.UTC).getMillis, 42, EventType.CLICK))

    val result = statistics.getStatistic(DateTime.now(DateTimeZone.UTC))
    result.uniqueUsers.get shouldBe 1
  }
  "save event" should "update cache and push to persistent storage" in {
    val mockStatistics = mock(classOf[Statistics])
    val mockEventPersistence = mock(classOf[EventPersistence])
    val mockStatisticsStorage = mock(classOf[StatisticsStorage])
    val eventStorage = new EventStorage{
      _statistics = mockStatistics
      _statisticsStorage = mockStatisticsStorage
      _eventPersistence = mockEventPersistence
    }
    val event = Event(1,1,EventType.CLICK)

    eventStorage.saveEvent(event)

    verify(mockStatisticsStorage).maintainCacheAlignment()
    verify(mockStatistics).updateStatisticsCache(event)
    verify(mockEventPersistence).save(event)
  }
}
