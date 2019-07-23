import scala.collection.mutable

object Singletons{
  var singletonMap = mutable.Map[Class[_],Object]()
  def getSingleton[T](clz: Class[T]) = singletonMap.getOrElse(clz,null).asInstanceOf[T]
  def setSingleton[T <: Object](singleton: T) = singletonMap.put(singleton.getClass,singleton)
}

trait Dependencies {
  var _statistics :Statistics = null
  def statistics: Statistics = {
    if (_statistics==null) _statistics = new Statistics
    _statistics
  }

  var _statisticsStorage :StatisticsStorage = null
  def statisticsStorage: StatisticsStorage = {
    if (_statisticsStorage == null) {
      synchronized {
        _statisticsStorage = Singletons.getSingleton(classOf[StatisticsStorage])
        if (_statisticsStorage == null) {
          _statisticsStorage = new StatisticsStorage
          Singletons.setSingleton(_statisticsStorage)
        }
      }
    }
    _statisticsStorage
  }

  var _eventStorage :EventStorage = null
  def eventStorage = {
    if (_eventStorage==null) _eventStorage = new EventStorage
    _eventStorage
  }

  var _eventPersistence :EventPersistence = null
  def eventPersistence = {
    if (_eventPersistence == null) {
      synchronized {
        _eventPersistence = Singletons.getSingleton(classOf[EventPersistence])
        if (_eventPersistence == null) {
          _eventPersistence = new EventPersistence
          Singletons.setSingleton(_eventPersistence)
        }
      }
    }
    _eventPersistence
  }
}
