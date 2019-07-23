import java.sql.DriverManager

import org.joda.time.DateTime

object EventPersistence{
  def clear() ={
    Class.forName("org.h2.Driver")
    val connection = DriverManager.getConnection("jdbc:h2:~/basic-analytics-data")
    try {
      connection.prepareStatement("drop table if exists event").execute()
      connection.prepareStatement(
        "create table " +
          "     event (" +
          "        id bigint auto_increment, " +
          "    userid bigint, " +
          " timestamp bigint, " +
          " eventtype varchar(25), " +
          "   primary key(id)" +
          "           )").execute()
    } finally {
      connection.close()
    }
  }
}
class EventPersistence {

  EventPersistence.clear()

  def save(event: Event) = {
    val connection = DriverManager.getConnection("jdbc:h2:~/basic-analytics-data")
    try{
      val saveStatement = connection.prepareStatement(
        "insert into event (userid,timestamp,eventtype) values (?,?,?)")

      saveStatement.setLong(1, event.userId)
      saveStatement.setLong(2, event.timestamp)
      saveStatement.setString(3, event.eventType.toString)

      saveStatement.execute()
    }finally{
      connection.close()
    }
  }

  def loadHourlyStatistic(dateTime: DateTime) :(Option[Long],Option[Long],Option[Long]) = {
    val connection = DriverManager.getConnection("jdbc:h2:~/basic-analytics-data")
    try {
      val startOfHour = dateTime.hourOfDay.roundFloorCopy.getMillis
      val endOfHour = dateTime.hourOfDay.roundCeilingCopy.getMillis

      val saveStatement = connection.prepareStatement(
        "select " +
          "(select count(distinct userid) " +
          "   from event" +
          "  where timestamp >= ? " +
          "    and timestamp <= ?) as uniqueUsers," +
          "(select count(*) " +
          "   from event" +
          "  where eventtype='click'" +
          "    and timestamp >= ? " +
          "    and timestamp <= ?) as clicks," +
          "(select count(*)" +
          "   from event" +
          "  where eventtype='impression'" +
          "    and timestamp >= ?" +
          "    and timestamp <= ?) as impressions")

      for (i <- 1 to 3) {
        saveStatement.setLong((i * 2) - 1, startOfHour)
        saveStatement.setLong(i * 2, endOfHour)
      }

      val result = saveStatement.executeQuery()
      result.next()
      (Some(result.getLong("uniqueUsers")),
        Some(result.getLong("clicks")),
        Some(result.getLong("impressions")))
    }catch{
      case t: Throwable => {
        t.printStackTrace()
        (None,None,None)
      }
    }finally{
      connection.close()
    }
  }
}
