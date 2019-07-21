import java.sql.DriverManager

class EventPersistence {
  Class.forName("org.h2.Driver")
  val connection = DriverManager.getConnection("jdbc:h2:~/basic-analytics-data")
  connection.prepareStatement("drop table event").execute()
  connection.prepareStatement(
    "create table " +
      "     event (" +
      "        id bigint auto_increment, " +
      "    userid bigint, " +
      " timestamp bigint, " +
      " eventtype varchar(25), " +
      "   primary key(id)" +
      "           )").execute()

  connection.close()

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
}
