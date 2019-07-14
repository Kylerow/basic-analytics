import EventType.EventType
import org.joda.time.DateTime

import scala.collection.mutable

object EventType extends Enumeration{
  type EventType = Value
  def apply(eventType: String) = {
    eventType match {
      case "click" => CLICK
      case "impression" => IMPRESSION
      case _ => UNKNOWN
    }
  }
  val CLICK, IMPRESSION, UNKNOWN = Value
}

case class Statistic(uniqueUsers: Option[Long], clicks: Option[Long], impressions: Option[Long])
case class Event(timestamp: Long, userId: Long, eventType: EventType)

object EventStorage{
  var statistics = mutable.Map[String,Long]()
  var users = mutable.Set[Long]()
  var currentHour :(Int,Int,Int) = AnalyticsTiming.getHour()
  def clear() = {
    statistics = mutable.Map[String,Long]()
    users = mutable.Set[Long]()
  }
}

class EventStorage {
  def saveEvent(event: Event) = {
    val storageName = storageBucketName(event)

    maintainCacheAlignment()

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

  def maintainCacheAlignment() = {
    if (EventStorage.currentHour!=AnalyticsTiming.getHour()){
      EventStorage.clear()
      EventStorage.currentHour = AnalyticsTiming.getHour()
    }
  }

  val storageBucketName: Event=>String = {
    case Event(_, _, EventType.CLICK) => "clicks"
    case Event(_, _, EventType.IMPRESSION) => "impressions"
  }
}
