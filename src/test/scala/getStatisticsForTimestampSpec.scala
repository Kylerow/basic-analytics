import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}
import org.mockito.Mockito._
import org.mockito.Matchers._

class getStatisticsForTimestampSpec extends FlatSpec with Matchers {
  "getStatisticsForTimestamp" should "convert cached stats into csv" in {
    val mockStatisticStorage = mock(classOf[EventStorage])
    val statistic = Statistic(Some(5),Some(100),Some(2000))
    when(
      mockStatisticStorage
        .getStatistic(
          any(classOf[DateTime])
        )
    ).thenReturn(statistic)

    when(
      mockStatisticStorage
        .asCsv(
          statistic
        )
    ).thenReturn("it worked...")

    getStatisticsForTimestamp.eventStorage = mockStatisticStorage

    val result = getStatisticsForTimestamp(1L)
    result shouldBe "it worked..."
  }
}
