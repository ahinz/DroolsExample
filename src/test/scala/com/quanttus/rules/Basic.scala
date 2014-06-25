package com.quanttus.rules

import com.github.nscala_time.time.Imports._
import scala.io._

import org.kie.api._
import org.kie.api.builder._
import org.kie.api.runtime._

import org.scalatest._

trait DroolsInit {
  val drl = Source.fromURL(getClass.getResource("/com/quanttus/rules/helloWorld.drl")).mkString("")

  def initDrools(): KieSession = {
    val kieServices = KieServices.Factory.get()
    val kfs = kieServices.newKieFileSystem()
    kfs.write("src/main/resources/simple.drl", drl)

    val kieBuilder = kieServices.newKieBuilder(kfs).buildAll()
    val results = kieBuilder.getResults()

    if (results.hasMessages(Message.Level.ERROR)) {
      throw new RuntimeException(results.getMessages().toString())
    }
    val kieContainer =
      kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId())
    val kieBase = kieContainer.getKieBase()
    kieContainer.newKieSession()
  }
}

class RulesSpec extends FlatSpec with Matchers with DroolsInit {

  it should "run a simple rule" in {
    val session = initDrools()

    val userWithHourlyDataPoints = User(
      Seq(
        DailyDataPoint(5000, 20.0),
        DailyDataPoint(6000, 20.0),
        DailyDataPoint(1000, 20.0),
        DailyDataPoint(1000, 20.0),
        DailyDataPoint(1000, 20.0),
        DailyDataPoint(1000, 10.0),
        DailyDataPoint(1000, 10.0)
      ),
      true, 90, 120.2, 19)

    val userWithFewDataPoints = User(
      Seq(
        DailyDataPoint(5000, 5.0),
        DailyDataPoint(1000, 5.0)),
      true, 87, 120.3, 20)

    val notifier = new ListNotifier()
    session.setGlobal("notifier", notifier)

    session.insert(userWithHourlyDataPoints)
    session.insert(userWithFewDataPoints)

    // val workingMemory = initializeMessageObjects(ruleBase)
    val actualNumberOfRulesFired = session.fireAllRules()

    println(notifier)

    actualNumberOfRulesFired should be (3)
  }

}
