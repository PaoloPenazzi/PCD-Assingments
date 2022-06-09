import SimulationActor.Command.{PositionDoneResponse, VelocityDoneResponse}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import SimulationActor.Command

import scala.collection.mutable

case class Simulation(numBodies: Int,
                      var iteration: Int,
                      sideLength: Int,
                      var bodies: mutable.Seq[Body]):
  val boundary: Boundary =
    Boundary(-sideLength, -sideLength, sideLength, sideLength)

object SimulationActor:
  var responseCounter = 0
  val actorsList: List[ActorRef[Command]] = List.empty

  enum Command:
    case VelocityDoneResponse(result: Body)
    case PositionDoneResponse(result: Body)
    case ComputeVelocityRequest(body: Body, bodies: mutable.Seq[Body], replyTo: ActorRef[VelocityDoneResponse])
    case ComputePositionRequest(body: Body, boundary: Boundary, replyTo: ActorRef[PositionDoneResponse])
    case UpdateGUI(bodies: mutable.Seq[Body])
    export Command.*

  def apply(simulation: Simulation): Behavior[Command] =
    Behaviors.receive { (context, msg) =>
      msg match
        case VelocityDoneResponse(result) =>
          simulation.bodies.update(result.id, result)
          responseCounter = responseCounter + 1
          if (responseCounter == simulation.bodies.size)
            responseCounter = 0
            for
              x <- simulation.bodies
              y <- actorsList
            yield y ! Command.ComputeVelocityRequest(x, simulation.bodies, context.self)
          Behaviors.same
        case PositionDoneResponse(result) =>
          simulation.bodies.update(result.id, result)
          responseCounter = responseCounter + 1
          if (responseCounter == simulation.bodies.size)
            responseCounter = 0
            if (simulation.iteration != 0)
              simulation.iteration = simulation.iteration - 1
              for
                x <- simulation.bodies
                y <- actorsList
              yield y ! Command.ComputePositionRequest(x, simulation.boundary, context.self)
              Behaviors.same
            else Behaviors.stopped
          else Behaviors.same
        case _ => throw IllegalStateException()
    }


object BodyActor:

  import SimulationActor.Command.*

  def apply(): Behavior[Command] =
    Behaviors.receive { (context, msg) =>
      msg match
        case ComputeVelocityRequest(body, bodies, ref) =>
          ref ! Command.VelocityDoneResponse(computeBodyVelocity(body, bodies))
          Behaviors.same
        case ComputePositionRequest(body, boundary, ref) =>
          ref ! Command.PositionDoneResponse(computeBodyPosition(body, boundary))
          Behaviors.same
        case _ => throw new IllegalStateException()
    }

  def computeBodyVelocity(body: Body, bodies: mutable.Seq[Body]): Body =
    var totalForce: Velocity2d = Velocity2d(0,0)
    bodies.filter((b) => !b.equals(body)).foreach((b) =>totalForce = totalForce.sum(body.computeRepulsiveForceBy(b)))
    totalForce = totalForce.sum(body.getCurrentFrictionForce)
    val acceleration: Velocity2d = Velocity2d(totalForce).scalarMul(1.0 / body.mass)
    body.updateVelocity(acceleration, 0.001) // TODO deltaTime hard coded
    body

  def computeBodyPosition(body: Body, boundary: Boundary): Body =
    body.updatePosition(0.001)
    body.checkAndSolveBoundaryCollision(boundary)
    body