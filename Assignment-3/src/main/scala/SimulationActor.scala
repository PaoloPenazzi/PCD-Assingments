import SimulationActor.Command.{PositionDoneResponse, VelocityDoneResponse}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.mutable

case class Simulation(numBodies: Int, var iteration: Int, sideLength: Int, var bodies: mutable.Seq[Body])

object SimulationActor:
  var responseCounter = 0
  val actorsList: List[ActorRef] = List.empty // TODO

  enum Command:
    case VelocityDoneResponse(result: Body)
    case PositionDoneResponse(result: Body)
    case ComputeVelocityRequest(body: Body, bodies: mutable.Seq[Body], replyTo: ActorRef[VelocityDoneResponse])
    case ComputePositionRequest(body: Body, bodies: mutable.Seq[Body], replyTo: ActorRef[VelocityDoneResponse])
    case UpdateGUI(bodies: mutable.Seq[Body])
    export Command.*

  def apply(simulation: Simulation): Behavior[Command] =
    Behaviors.receive { (context, msg) =>
      msg match
        case VelocityDoneResponse(result) =>
          simulation.bodies.update(result.id, result)
          if (incrementResponseCounter())
            for
              x <- simulation.bodies
              y <- actorsList
            yield y ! Command.ComputeVelocityRequest(x, simulation.bodies, context.self)
          else Behaviors.same
        case PositionDoneResponse(result) =>
          simulation.bodies.update(result.id, result)
          if (incrementResponseCounter())
            if (simulation.iteration != 0)
              for
                x <- simulation.bodies
                y <- actorsList
              yield y ! Command.ComputePositionRequest(x, simulation.bodies, context.self)
          else Behaviors.same
    }

    private def incrementResponseCounter(): Boolean =
      responseCounter = responseCounter + 1
      if(responseCounter == simulation.numBodies)
        responseCounter = 0
        true

  // TODO
  // seconda apply con la gui...
  // def apply(simulation: Simulation, )


@main
def main(): Unit = {
  println(Simulation(100,100,100, mutable.Seq.empty))
}
