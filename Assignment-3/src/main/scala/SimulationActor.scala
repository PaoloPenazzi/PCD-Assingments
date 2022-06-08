import SimulationActor.Command.{PositionDoneResponse, VelocityDoneResponse}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.mutable

case class Simulation(numBodies: Int, var iteration: Int, sideLength: Int, var bodies: mutable.Seq[Body])

object SimulationActor:
  var responseCounter = 0
  enum Command:
    case VelocityDoneResponse(result: Body)
    case PositionDoneResponse(result: Body)
    case ComputeVelocityRequest(body: Body, bodies: mutable.Seq[Body], replyTo: ActorRef[VelocityDoneResponse])
    case ComputeRequest(body: Body, bodies: mutable.Seq[Body], replyTo: ActorRef[VelocityDoneResponse])
    case UpdateGUI(bodies: mutable.Seq[Body])
    export Command.*

  def apply(simulation: Simulation): Behavior[Command] =
    Behaviors.receive { (context, msg) =>
      msg match
        case VelocityDoneResponse(result) =>
          // aggiorno la lista con il body ricevuto
          simulation.bodies.update(result.id, result)
          responseCounter = responseCounter + 1
          // controllo se ho ricevuto tutti i messaggi di velocita
          if (responseCounter == simulation.numBodies)
            for
              x <- simulation.bodies
              y <- ???
            yield y ! Command.ComputeVelocityRequest(x, simulation.bodies, context.self)
          // se si allora mando i messaggi di posizione
          // se no non faccio un cazzo == Behavior.same
          ???
        case PositionDoneResponse(_) =>
          // aggiorno la lista con il body ricevuto
          // controllo se ho ricevuto tutti i messaggi di position
          // se si allora mando i messaggi di velocita
          // aggiorno la struttura dati della simulazione e controllo se ho finito le iterazioni
          // se no non faccio un cazzo == Behavior.same
          ???
    }

  // TODO
  // seconda apply con la gui...
  // def apply(simulation: Simulation, )


@main
def main(): Unit = {
  println(Simulation(100,100,100, mutable.Seq.empty))
}
