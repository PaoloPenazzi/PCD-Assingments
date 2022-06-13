package it.unibo.pcd.assignment

import akka.actor.Kill
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.mutable
import scala.util.Random

object SimulationActor:
  var responseCounter = 0
  var virtualTime = 0.0
  var actorsList: mutable.Seq[ActorRef[Message]] = mutable.Seq.empty
  var view: Option[ActorRef[Message]] = None
  // var initialTime: Long = 0

  def apply(simulation: Simulation, gui: Boolean): Behavior[Message] =
    Behaviors.receive { (context, msg) =>
      msg match
        case StopSimulation() =>
          onPause(simulation, gui)
        case StartSimulation() =>
          // initialTime = System.currentTimeMillis()
          if gui then {view = Some(context.spawn(ViewActor(context.self), "view-actor")); view.get ! StartGUI()}
          for (n <- 0 until simulation.numBodies)
            val newActor = context.spawn(BodyActor(simulation.bodies(n)), "actor-number-"+ n.toString)
            actorsList = actorsList :+ newActor
            newActor ! ComputeVelocityRequest(simulation.bodies, context.self)
          Behaviors.same
        case VelocityDoneResponse(result) =>
          simulation.bodies(result.id).velocity = result.velocity
          responseCounter = responseCounter + 1
          if (responseCounter == simulation.bodies.size)
            responseCounter = 0
            actorsList.foreach(y => y ! ComputePositionRequest(simulation.boundary, context.self))
          Behaviors.same
        case PositionDoneResponse(result) =>
          simulation.bodies(result.id).position = result.position
          responseCounter = responseCounter + 1
          if (responseCounter == simulation.bodies.size)
            responseCounter = 0
            virtualTime = virtualTime + 0.001
            if view.isDefined then view.get ! UpdateGUI(simulation.bodies, virtualTime, simulation.iteration, simulation.boundary)
            if (simulation.iteration != 0)
              simulation.iteration = simulation.iteration - 1
              actorsList.foreach(y => y ! ComputeVelocityRequest(simulation.bodies, context.self))
              Behaviors.same
            else
              // val finalTime: Long = System.currentTimeMillis()
              // println("Tempo: " + (finalTime - initialTime) + " ms")
              Behaviors.stopped
          else Behaviors.same
        case _ => Behaviors.same
    }

  def onPause(simulation: Simulation, gui: Boolean): Behavior[Message] =
    Behaviors.setup( ctx => {
      Behaviors.withStash[Message](10000){ stash =>
      Behaviors.receiveMessage{
            case ResumeSimulation() =>
              stash.unstashAll(SimulationActor(simulation, gui))
            case StopSimulation() =>
              Behaviors.same
            case other =>
              stash.stash(other)
              Behaviors.same
        }
      }
    })


object BodyActor:
  def apply(body: Body): Behavior[Message] =
    Behaviors.receive { (_, msg) =>
      msg match
        case ComputeVelocityRequest(bodies, ref) =>
          body.computeBodyVelocity(bodies)
          ref ! VelocityDoneResponse(body)
          Behaviors.same
        case ComputePositionRequest(bounds, ref) =>
          body.computeBodyPosition(bounds)
          ref ! PositionDoneResponse(body)
          Behaviors.same
        case _ => throw new IllegalStateException()
    }
    
    

