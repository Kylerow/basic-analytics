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
  val statistics = mutable.Map[String,Long]()
}

class EventStorage {

  val users = mutable.Set[Long]()
  def saveEvent(event: Event) = {
    event match {
      case Event(timestamp, userid, _) => {
        if(users.add(userid)) EventStorage.statistics.put("uniqueUsers",users.size)
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
