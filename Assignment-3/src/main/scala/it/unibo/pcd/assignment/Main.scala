package it.unibo.pcd.assignment

import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success


@main
def main(): Unit = {
  val system: ActorSystem[Message] = ActorSystem(SimulationActor(Simulation(1000, 10000,
    8), true), name = "simulation")
  system ! StartSimulation()
}