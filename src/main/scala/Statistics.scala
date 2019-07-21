import org.joda.time.DateTime

case class Statistic(uniqueUsers: Option[Long], clicks: Option[Long], impressions: Option[Long])

class Statistics extends Dependencies {
  def updateStatisticsCache(event: Event) = {
    statisticsStorage.statistics.synchronized {
      val storageName = storageBucketName(event)
      if (AnalyticsTiming.isCurrentHour(event.timestamp)) {
        val events =
          statisticsStorage
            .statistics
            .getOrElse[Long](storageName, 0)
        statisticsStorage.statistics.put(storageName, events + 1)

        if (statisticsStorage.users.add(event.userId))
          statisticsStorage.statistics.put("uniqueUsers", statisticsStorage.users.size)
      }
    }
  }

  def getStatistic(dateTime: DateTime) :Statistic = {
    if (AnalyticsTiming.isCurrentHour(dateTime))
      getCachedStatistic(dateTime)
    else
      Statistic.tupled(eventPersistence.loadHourlyStatistic(dateTime))
  }

  def getCachedStatistic(dateTime: DateTime) :Statistic = {
    statisticsStorage.maintainCacheAlignment()
    Statistic(
      statisticsStorage.statistics.get("uniqueUsers"),
      statisticsStorage.statistics.get("clicks"),
      statisticsStorage.statistics.get("impressions"))
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
    case _ => "unknowns"
  }
}
