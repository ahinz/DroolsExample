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

  def randomf(base: Double, dev: Double) =
    () => util.Random.nextGaussian() * dev + base

  def insertUserAndFacts(session: KieSession) {
    val f = randomf(100, 30)

    val user = User(
      true, f(), f(), randomf(40, 20)())

    // Creating 2 weeks for of data, biasing around 3000 cal (+/- 1000)
    val cals = randomf(3000, 1000)

    // Each day has a validity period between 18 hours +/- 6
    val hours = randomf(18, 6)

    session.insert(user)

    val numberOfDataPoints = 16
    for(i <- 0 until numberOfDataPoints) {
      session.insert(DailyDataPoint(user, cals().toInt, hours(), i))
    }

    session.fireAllRules()
  }

  it should "be performant" in {
    val notifier = new ListNotifier()

    val session = initDrools()
    session.setGlobal("notifier", notifier)

    session.insert(Now(10))

    val numberOfUsers = 100
    for(i <- 0 until numberOfUsers) {
      insertUserAndFacts(session)
    }

    session.fireAllRules()

    println("Notices: " + notifier.messages.length)
  }



  // it should "run a simple rule" in {
  //   val session = initDrools()

  //   val userWithHourlyDataPoints = User(
  //     true, 90, 120.2, 19)

  //   val userWithFewDataPoints = User(
  //     true, 87, 120.3, 20)

  //   val user1Pts = Seq(
  //       DailyDataPoint(userWithFewDataPoints, 5000, 5.0, 10),
  //       DailyDataPoint(userWithFewDataPoints, 1000, 5.0, 9))

  //   val user2Pts = Seq(
  //     DailyDataPoint(userWithHourlyDataPoints, 5000, 20.0, 10),
  //       DailyDataPoint(userWithHourlyDataPoints, 4000, 20.0, 9),
  //       DailyDataPoint(userWithHourlyDataPoints, 1000, 20.0, 8),
  //       DailyDataPoint(userWithHourlyDataPoints, 1000, 20.0, 7),
  //       DailyDataPoint(userWithHourlyDataPoints, 1000, 20.0, 6),
  //       DailyDataPoint(userWithHourlyDataPoints, 1000, 10.0, 5),
  //       DailyDataPoint(userWithHourlyDataPoints, 1000, 10.0, 4))

  //   val notifier = new ListNotifier()
  //   session.setGlobal("notifier", notifier)

  //   session.insert(Now(10))
  //   session.insert(userWithHourlyDataPoints)
  //   session.insert(userWithFewDataPoints)

  //   for (i <- user1Pts) session.insert(i)
  //   for (i <- user2Pts) session.insert(i)

  //   // val workingMemory = initializeMessageObjects(ruleBase)
  //   val actualNumberOfRulesFired = session.fireAllRules()

  //   //println(notifier)

  //   //actualNumberOfRulesFired should be (3)
  // }

}
