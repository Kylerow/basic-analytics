import org.joda.time.DateTime

import scala.collection.mutable

object StatisticsStorage {
  var statistics = mutable.Map[String,Long]()
  var users = mutable.Set[Long]()
  var currentHour :(Int,Int,Int) = AnalyticsTiming.getHour()
  def clear() = {
    statistics = mutable.Map[String,Long]()
    users = mutable.Set[Long]()
  }
}
case class Statistic(uniqueUsers: Option[Long], clicks: Option[Long], impressions: Option[Long])
class Statistics {
  def updateStatisticsCache(event: Event) = {
    val storageName = storageBucketName(event)
    if (AnalyticsTiming.isCurrentHour(event.timestamp)) {
      val events =
        StatisticsStorage
          .statistics
          .getOrElse[Long](storageName, 0)
      StatisticsStorage.statistics.put(storageName, events + 1)

      if (StatisticsStorage.users.add(event.userId))
        StatisticsStorage.statistics.put("uniqueUsers", StatisticsStorage.users.size)
    }
  }

  def maintainCacheAlignment() = {
    if (StatisticsStorage.currentHour!=AnalyticsTiming.getHour()){
      StatisticsStorage.clear()
      StatisticsStorage.currentHour = AnalyticsTiming.getHour()
    }
  }

  def getStatistic(dateTime: DateTime) :Statistic = {
    maintainCacheAlignment()

    Statistic(
      StatisticsStorage.statistics.get("uniqueUsers"),
      StatisticsStorage.statistics.get("clicks"),
      StatisticsStorage.statistics.get("impressions"))
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
