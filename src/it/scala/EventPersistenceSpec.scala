import java.sql.DriverManager

import org.scalatest.{FlatSpec, Matchers}

class EventPersistenceSpec
  extends FlatSpec with Matchers {
  "save" should "put entity in database table" in {
    val eventPersistence = new EventPersistence
    val event = Event(276,452,EventType.withName("click"))
    eventPersistence.save(event)

    val connection = DriverManager.getConnection("jdbc:h2:~/basic-analytics-data")
    val statement = connection.createStatement()
    statement.execute(
      "select count(*) " +
      "  from event " +
      " where userid=452 " +
      "   and timestamp=276" +
      "   and eventtype='click'")

    val resultSet = statement.getResultSet
    resultSet.next
    resultSet.getInt(1) shouldBe 1
    connection.close()
  }
}
