import org.joda.time.DateTime

object AnalyticsTiming {
  var getHour = ()=>{
    val now = DateTime.now
    (now.year().get(),now.dayOfYear().get(),now.hourOfDay().get())
  }

  def isCurrentHour(timestamp: Long) = {
    val comparisonDate = new DateTime(timestamp)
    (comparisonDate.year().get(),
      comparisonDate.dayOfYear().get(),
      comparisonDate.hourOfDay().get()) == getHour()
  }

  def setHourToPresentPlusOne() = {
    getHour = ()=>{
      val now = DateTime.now
      (now.year().get(),now.dayOfYear().get(),now.hourOfDay().get()+1)
    }
  }
}
