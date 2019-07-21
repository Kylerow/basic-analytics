import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}
import org.mockito.Mockito._
import org.mockito.Matchers._

class getStatisticsForTimestampSpec extends FlatSpec with Matchers {
  "getStatisticsForTimestamp" should "convert cached stats into csv" in {
    val mockStatistics = mock(classOf[Statistics])
    val statistic = Statistic(Some(5),Some(100),Some(2000))
    when(
      mockStatistics
        .getStatistic(
          any(classOf[DateTime])
        )
    ).thenReturn(statistic)

    when(
      mockStatistics
        .asCsv(
          statistic
        )
    ).thenReturn("it worked...")

    getStatisticsForTimestamp._statistics = mockStatistics

    val result = getStatisticsForTimestamp(1L)
    result shouldBe "it worked..."
  }
}
