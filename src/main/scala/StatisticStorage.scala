import java.util

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
  def clear() = {
    statistics = mutable.Map[String,Long]()
    users = mutable.Set[Long]()
  }
}

class EventStorage {
  def saveEvent(event: Event) = {
    event match {
      case Event(timestamp, userid, EventType.CLICK) => {
        val clicks =
          EventStorage
            .statistics
            .getOrElse[Long]("clicks",0)
        EventStorage.statistics.put("clicks",clicks+1)
        if(EventStorage.users.add(userid))
          EventStorage.statistics.put("uniqueUsers",EventStorage.users.size)
      }
      case Event(timestamp, userid, EventType.IMPRESSION) => {
        val impressions =
          EventStorage
            .statistics
            .getOrElse[Long]("impressions",0)
        EventStorage.statistics.put("impressions",impressions+1)
        if(EventStorage.users.add(userid))
          EventStorage.statistics.put("uniqueUsers",EventStorage.users.size)
      }
    }
  }
  def getStatistic(dateTime: DateTime) :Statistic = {
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
}
