import EventType.EventType

object EventType extends Enumeration{
  type EventType = Value
  val CLICK = Value("click")
  val IMPRESSION = Value("impression")
  val UNKNOWN = Value("unknown")
}

case class Event(timestamp: Long, userId: Long, eventType: EventType)

class EventStorage extends Dependencies {
  def saveEvent(event: Event) = {
    statisticsStorage.maintainCacheAlignment()
    statistics.updateStatisticsCache(event)
    eventPersistence.save(event)
  }
}
