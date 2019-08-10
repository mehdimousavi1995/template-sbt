
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.{Config, ConfigFactory}
import http.JsonSerializer
import org.scalatest.{WordSpec, _}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import persist.postgres.PostgresDBExtension
import sdk.CustomConfig

import scala.concurrent.duration.FiniteDuration

trait BaseAppSuite extends WordSpec
  with ScalaFutures
  with Matchers
  with Inside
  with BeforeAndAfterAll
  with ScalatestRouteTest {

  override protected def createActorSystem(): ActorSystem = {
    val defaults: Config = ConfigFactory.empty()
    val config: Config = CustomConfig.load()
    ActorSystem("template", config)
  }


  val pdb = PostgresDBExtension(system)

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(30, Seconds), interval = Span(100, Millis))
  val finiteDuration = FiniteDuration(30, TimeUnit.SECONDS)

  override def beforeAll(): Unit = {
    // before running all tests this block of code being executed.
  }

  override def afterAll(): Unit = {
    // after running all tests this block of code being executed.
    pdb.clean()

  }

}
