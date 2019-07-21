import org.joda.time.{DateTime, DateTimeZone}

object getStatisticsForTimestamp extends Dependencies {
  def apply(timestamp: Long) :String = {
    val statistic = statistics.getStatistic(new DateTime(timestamp,DateTimeZone.UTC))
    statistics.asCsv(statistic)
  }
}
