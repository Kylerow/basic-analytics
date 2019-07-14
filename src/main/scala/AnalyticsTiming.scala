import org.joda.time.DateTime

object AnalyticsTiming {
  def getHour() ={
    val now = DateTime.now
    (now.year(),now.dayOfYear(),now.hourOfDay())
  }
  def isCurrentHour(timestamp: Long) = {
    val comparisonDate = new DateTime(timestamp)
    (comparisonDate.year(),comparisonDate.dayOfYear(),comparisonDate.hourOfDay()) == getHour()
  }
}
