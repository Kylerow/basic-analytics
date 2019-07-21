import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{FlatSpec, Matchers}
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._

class StatisticsSpec
  extends FlatSpec with Matchers {

  "getStatistic" should "get cached statistics if it's a request for current hour" in {
    val now = DateTime.now(DateTimeZone.UTC)
    val mockStatistics = mock(classOf[Statistics])
    val mockEventPersistence = mock(classOf[EventPersistence])
    when(mockStatistics.eventPersistence).thenReturn(mockEventPersistence)
    when(mockStatistics.getStatistic(any())).thenCallRealMethod()

    mockStatistics.getStatistic(now)

    verify(mockStatistics).getCachedStatistic(now)
    verify(mockEventPersistence,never()).loadHourlyStatistic(any())
  }

  "getStatistic" should "get persistent statistics if it's a request for a past hour" in {
    val laterOn = DateTime.now().hourOfDay().addToCopy(-2)
    val mockEventPersistence = mock(classOf[EventPersistence])
    val mockStatistics = mock(classOf[Statistics])

    when(mockStatistics.eventPersistence).thenReturn(mockEventPersistence)
    when(mockStatistics.getStatistic(any())).thenCallRealMethod()
    when(mockEventPersistence.loadHourlyStatistic(laterOn))
      .thenReturn((Some(1L),Some(1L),Some(1L)))

    mockStatistics.getStatistic(laterOn)

    verify(mockStatistics,never()).getCachedStatistic(laterOn)
    verify(mockEventPersistence).loadHourlyStatistic(laterOn)
  }
}
