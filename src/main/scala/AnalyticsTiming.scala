import org.joda.time.{DateTime, DateTimeZone}

object AnalyticsTiming {
  var getHour = ()=>{
    val now = DateTime.now(DateTimeZone.UTC)
    (now.year().get(),now.dayOfYear().get(),now.hourOfDay().get())
  }

  def isCurrentHour(timestamp: Long) = {
    val comparisonDate = new DateTime(timestamp,DateTimeZone.UTC)
    (comparisonDate.year().get(),
      comparisonDate.dayOfYear().get(),
      comparisonDate.hourOfDay().get()) == getHour()
  }

  def isCurrentHour(comparisonDate: DateTime) = {
    (comparisonDate.year().get(),
      comparisonDate.dayOfYear().get(),
      comparisonDate.hourOfDay().get()) == getHour()
  }

  def setHourToPresentPlusOne() = {
    getHour = ()=>{
      val now = DateTime.now(DateTimeZone.UTC)
      (now.year().get(),now.dayOfYear().get(),now.hourOfDay().get()+1)
    }
  }
}
