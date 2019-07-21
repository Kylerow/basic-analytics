import scala.collection.mutable

class StatisticsStorage {
  var statistics = mutable.Map[String,Long]()
  var users = mutable.Set[Long]()
  var currentHour :(Int,Int,Int) = AnalyticsTiming.getHour()
  def clear() = {
    statistics = mutable.Map[String,Long]()
    users = mutable.Set[Long]()
  }
  def maintainCacheAlignment() = {
    if (currentHour!=AnalyticsTiming.getHour()){
      clear()
      currentHour = AnalyticsTiming.getHour()
    }
  }
}

