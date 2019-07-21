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


case class Event(timestamp: Long, userId: Long, eventType: EventType)

object EventStorage {
  var statistics = mutable.Map[String,Long]()
  var users = mutable.Set[Long]()
  var currentHour :(Int,Int,Int) = AnalyticsTiming.getHour()
  def clear() = {
    statistics = mutable.Map[String,Long]()
    users = mutable.Set[Long]()
  }
}

class EventStorage extends Dependencies {

  def saveEvent(event: Event) = {
    statistics.maintainCacheAlignment()
    statistics.updateStatisticsCache(event)
  }

}
