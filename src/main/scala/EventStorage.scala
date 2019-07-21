import EventType.EventType

import scala.collection.mutable

object EventType extends Enumeration{
  type EventType = Value
  val CLICK = Value("click")
  val IMPRESSION = Value("impression")
  val UNKNOWN = Value("unknown")
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
    eventPersistence.save(event)
  }
}
