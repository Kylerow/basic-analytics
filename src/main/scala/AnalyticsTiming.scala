import org.joda.time.DateTime

object AnalyticsTiming {
  def getHour() = DateTime.now.hourOfDay()

}
