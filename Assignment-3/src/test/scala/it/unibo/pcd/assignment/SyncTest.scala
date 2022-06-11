package it.unibo.pcd.assignment

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import org.scalatest.funsuite.AnyFunSuite

class SyncTest extends AnyFunSuite :
  import it.unibo.pcd.assignment.*
  // TODOOOOO
  test("Test1") {
    val testKit = BehaviorTestKit(SimulationActor(Simulation(10, 10, 10), false))
    testKit.run(StartSimulation())
    testKit.expectEffect(Effect.Spawned(ViewActor(testKit.ref), "view-actor"))
  }
