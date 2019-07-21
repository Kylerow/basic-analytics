import org.joda.time.DateTime

case class Statistic(uniqueUsers: Option[Long], clicks: Option[Long], impressions: Option[Long])
class Statistics {
  def updateStatisticsCache(event: Event) = {
    val storageName = storageBucketName(event)
    if (AnalyticsTiming.isCurrentHour(event.timestamp)) {
      val events =
        EventStorage
          .statistics
          .getOrElse[Long](storageName, 0)
      EventStorage.statistics.put(storageName, events + 1)

      if (EventStorage.users.add(event.userId))
        EventStorage.statistics.put("uniqueUsers", EventStorage.users.size)
    }
  }

  def maintainCacheAlignment() = {
    if (EventStorage.currentHour!=AnalyticsTiming.getHour()){
      EventStorage.clear()
      EventStorage.currentHour = AnalyticsTiming.getHour()
    }
  }

  def getStatistic(dateTime: DateTime) :Statistic = {
    maintainCacheAlignment()

    Statistic(
      EventStorage.statistics.get("uniqueUsers"),
      EventStorage.statistics.get("clicks"),
      EventStorage.statistics.get("impressions"))
  }

  def asCsv(statistic: Statistic) :String = {
    s"""unique_users,${statistic.uniqueUsers.getOrElse(0)}
       |clicks,${statistic.clicks.getOrElse(0)}
       |impressions,${statistic.impressions.getOrElse(0)}
    """.stripMargin
  }

  val storageBucketName: Event=>String = {
    case Event(_, _, EventType.CLICK) => "clicks"
    case Event(_, _, EventType.IMPRESSION) => "impressions"
  }
}
