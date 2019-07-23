import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.apache.http.impl.client.HttpClients
import org.joda.time.{DateTime, DateTimeZone}
import scala.concurrent.duration._

import scala.util.Random


class ScaledSpecRunner extends AnalyticsIntegrationTest{
  "performance test with 50 users over 30 seconds" should "have a max response time of 3 seconds" in {
    val httpclient = HttpClients.createDefault
    httpclient.execute(clearDataUri)
    val props = new GatlingPropertiesBuilder
    props.resultsDirectory(System.getProperty("user.dir")+"/performance")
    props.binariesDirectory(System.getProperty("user.dir")+"target/scala-2.12/it-classes")
    props.simulationClass("ScaledSpec")
    httpclient.close()

    Gatling.fromMap(props.build) shouldBe 0
  }
}

class ScaledSpec extends Simulation {
  val httpConf = http
    .baseUrl("http://localhost:8080/analytics")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val USERS = 50
  val MAX_RESPONSE_TIME = 3000
  val TEST_TIME = 30 seconds
  val EVENTS = Array("click","impression")

  val scn = scenario("BasicSimulation")
    .randomSwitch(
      50d -> randomSwitch(
        95d -> exec(http("read_current_hour")
          .get("")
          .queryParam("timestamp", _ => DateTime.now(DateTimeZone.UTC).getMillis())),
        5d -> exec(http("read_previous_hour")
          .post("")
          .queryParam("timestamp", _ => (DateTime.now(DateTimeZone.UTC).getMillis - (1000 * 60 * 60)))
          .queryParam("user", _ => Random.nextInt(USERS))
          .queryParam("event", _ => EVENTS(Random.nextInt(2))))
      ),
      50d -> randomSwitch(
        95d -> exec(http("write_current_hour")
          .get("")
          .queryParam("timestamp", _ => DateTime.now(DateTimeZone.UTC).getMillis())),
        5d -> exec(http("write_previous_hour")
          .post("")
          .queryParam("timestamp", _ => (DateTime.now(DateTimeZone.UTC).getMillis - (1000 * 60 * 60)))
          .queryParam("user", _ => Random.nextInt(USERS))
          .queryParam("event", _ => EVENTS(Random.nextInt(2))))
      )
    )

  setUp(
     scn.inject(constantUsersPerSec(USERS) during (TEST_TIME))
  ).assertions(
    global.responseTime.max.lt(MAX_RESPONSE_TIME))
    .protocols(httpConf)
}
