import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import scala.util.Success


@main
def main(): Unit = {
  val system: ActorSystem[Command] = ActorSystem(SimulationActor(Simulation(5, 100,
    5), true), name = "simulation")
  system ! Command.StartSimulation
}