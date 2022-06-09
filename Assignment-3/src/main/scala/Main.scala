import SimulationActor.Command
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import scala.util.Success

@main
def main(): Unit = {
  var simulation = Simulation(20, 20, 20)
  /*val system: ActorSystem[Command] = ActorSystem(SimulationActor(Simulation(100, 1000,
    100)), name = "simulation")*/
  println(simulation.bodies)
  println(simulation.boundary)
}