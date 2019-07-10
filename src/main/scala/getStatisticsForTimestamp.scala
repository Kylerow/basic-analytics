import org.joda.time.{DateTime, DateTimeZone}

object getStatisticsForTimestamp {
  var eventStorage :EventStorage = null

  def getEventStorage()={
    if (eventStorage==null)
      eventStorage = new EventStorage
    eventStorage
  }

  def apply(timestamp: Long) :String = {
    val eventStorage = getEventStorage()
    val statistic = eventStorage.getStatistic(new DateTime(timestamp,DateTimeZone.UTC))
    eventStorage.asCsv(statistic)
  }
}
