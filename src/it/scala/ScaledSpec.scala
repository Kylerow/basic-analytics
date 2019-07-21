import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder
import io.gatling.core.Predef._
import io.gatling.http.Predef._


class ScaledSpecRunner extends AnalyticsIntegrationTest{
  "performance test" should "run" in {
    val props = new GatlingPropertiesBuilder
    props.resultsDirectory(System.getProperty("user.dir")+"/performance")
    props.binariesDirectory(System.getProperty("user.dir")+"target/scala-2.12/it-classes")
    props.simulationClass("ScaledSpec")
    Gatling.fromMap(props.build)
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

  val scn = scenario("BasicSimulation")
    .exec(http("request_1")
    .get("")
    .queryParam("timestamp","1234"))
    .pause(5)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}
