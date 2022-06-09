import akka.actor.typed.{ActorRef, ActorSystem, Behavior, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.mutable
import scala.util.Random

enum Command:
  case StartSimulation
  case VelocityDoneResponse(result: Body)
  case PositionDoneResponse(result: Body)
  case ComputeVelocityRequest(bodies: mutable.Seq[Body], replyTo: ActorRef[Command.VelocityDoneResponse])
  case ComputePositionRequest(boundary: Boundary, replyTo: ActorRef[Command.PositionDoneResponse])
  case UpdateGUI(bodies: mutable.Seq[Body])

case class Simulation(numBodies: Int,
                      var iteration: Int,
                      sideLength: Int):
  val boundary: Boundary =
    Boundary(-sideLength, -sideLength, sideLength, sideLength)
  var bodies: mutable.Seq[Body] =
    var myBodies: mutable.Seq[Body] = mutable.Seq.empty
    for (id <- 0 until numBodies)
      val x = boundary.x0 * 0.25 + Random.nextDouble() * (boundary.x1 - boundary.x0) * 0.25
      val y = boundary.y0 * 0.25 + Random.nextDouble() * (boundary.y1 - boundary.y0) * 0.25
      val body = Body(id, Position2d(x, y), Velocity2d(0, 0), 10)
      myBodies = myBodies :+ body
    myBodies

object SimulationActor:
  var responseCounter = 0
  var actorsList: mutable.Seq[ActorRef[Command]] = mutable.Seq.empty
  
  def apply(simulation: Simulation): Behavior[Command] =
    Behaviors.receive { (context, msg) =>
      msg match
        case Command.StartSimulation =>
          println("Simulation Started!")
          // creo gli attori
          for (n <- 0 until simulation.numBodies)
            val newActor = context.spawn(BodyActor(simulation.bodies(n)), "actor-number-"+ n.toString)
            actorsList = actorsList :+ newActor
            println(s"Actor: $newActor Number: $n")
            // mando i messaggi di partire a fare i calcoli
            newActor ! Command.ComputeVelocityRequest(simulation.bodies, context.self)
          Behaviors.same
        case Command.VelocityDoneResponse(result) =>
          println(s"VelocityDone for ${result.id}")
          simulation.bodies.update(result.id, result)
          responseCounter = responseCounter + 1
          if (responseCounter == simulation.bodies.size)
            responseCounter = 0
            actorsList.foreach(y => y ! Command.ComputePositionRequest(simulation.boundary, context.self))
          Behaviors.same
        case Command.PositionDoneResponse(result) =>
          println(s"PositionDone for ${result.id}")
          simulation.bodies.update(result.id, result)
          responseCounter = responseCounter + 1
          if (responseCounter == simulation.bodies.size)
            responseCounter = 0
            if (simulation.iteration != 0)
              simulation.iteration = simulation.iteration - 1
              actorsList.foreach(y => y ! Command.ComputeVelocityRequest(simulation.bodies, context.self))
              Behaviors.same
            else Behaviors.stopped
          else Behaviors.same
        case _ => throw IllegalStateException()
    }

object BodyActor:
  def apply(body: Body): Behavior[Command] =
    Behaviors.receive { (_, msg) =>
      msg match
        case Command.ComputeVelocityRequest(bodies, ref) =>
          println(s"VelocityRequest for ${body.id}")
          body.computeBodyVelocity(bodies)
          println(s"VelocityDone: Body ${body.id} Position ${body.position} Velocity ${body.velocity}")
          ref ! Command.VelocityDoneResponse(body)
          Behaviors.same
        case Command.ComputePositionRequest(boundary, ref) =>
          println(s"PositionRequest for ${body.id}")
          body.computeBodyPosition(boundary)
          println(s"PositionDone: Body ${body.id} Position ${body.position} Velocity ${body.velocity}")
          ref ! Command.PositionDoneResponse(body)
          Behaviors.same
        case _ => throw new IllegalStateException()
    }
    
  