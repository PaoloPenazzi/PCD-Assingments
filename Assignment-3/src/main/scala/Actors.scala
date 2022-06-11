import akka.actor.Kill
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.mutable
import scala.util.Random

object SimulationActor:
  var responseCounter = 0
  var actorsList: mutable.Seq[ActorRef[Message]] = mutable.Seq.empty
  var view: Option[ActorRef[Message]] = None

  def apply(simulation: Simulation, gui: Boolean): Behavior[Message] =
    Behaviors.receive { (context, msg) =>
      msg match
        case StopSimulation() =>
          // println("STOP BEHAVIOR")
          // Behaviors.stopped(() => println("STOOOOOOOOOOOOOOOOOOOOOOOOOOOOP"))
          onPause(simulation, gui)

        case StartSimulation() =>
          // println("Simulation Started!")
          // creo gli attori
          val dispatcherPath = "akka.actor.default-blocking-io-dispatcher"
          val props = DispatcherSelector.fromConfig(dispatcherPath)
          if gui then {view = Some(context.spawn(ViewActor(context.self), "view-actor", props)); view.get ! StartGUI()}
          for (n <- 0 until simulation.numBodies)
            val newActor = context.spawn(BodyActor(simulation.bodies(n)), "actor-number-"+ n.toString, props)
            actorsList = actorsList :+ newActor
            // println(s"Actor: $newActor Number: $n")
            // mando i messaggi di partire a fare i calcoli
            newActor ! ComputeVelocityRequest(simulation.bodies, context.self)
          Behaviors.same

        case VelocityDoneResponse(result) =>
          // println(s"VelocityDone for ${result.id}")
          simulation.bodies(result.id).velocity = result.velocity
          responseCounter = responseCounter + 1
          if (responseCounter == simulation.bodies.size)
            responseCounter = 0
            actorsList.foreach(y => y ! ComputePositionRequest(simulation.boundary, context.self))
          Behaviors.same

        case PositionDoneResponse(result) =>
          //println(s"PositionDone for ${result.id}")
          simulation.bodies(result.id).position = result.position
          responseCounter = responseCounter + 1
          if (responseCounter == simulation.bodies.size)
            responseCounter = 0
            view.get ! UpdateGUI(simulation.bodies, 0.001, simulation.iteration, simulation.boundary)
            if (simulation.iteration != 0)
              simulation.iteration = simulation.iteration - 1
              actorsList.foreach(y => y ! ComputeVelocityRequest(simulation.bodies, context.self))
              Behaviors.same
            else Behaviors.stopped
          else Behaviors.same

        case _ => Behaviors.same
    }

  def onPause(simulation: Simulation, gui: Boolean): Behavior[Message] =
    Behaviors.setup( ctx => {
      Behaviors.withStash[Message](10000){ stash =>
      Behaviors.receiveMessage{
            case ResumeSimulation() =>
              // println("RESUME SIMULATION")
              // println(stash.size)
              stash.unstashAll(SimulationActor(simulation, gui))

            case StopSimulation() =>
              Behaviors.same

            case other =>
              // println("YET IN PAUSE")
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
          // println(s"VelocityRequest for ${body.id}")
          body.computeBodyVelocity(bodies)
          // println(s"VelocityDone: Body ${body.id} Position ${body.position} Velocity ${body.velocity}")
          ref ! VelocityDoneResponse(body)
          Behaviors.same
        case ComputePositionRequest(boundary, ref) =>
          // println(s"PositionRequest for ${body.id}")
          body.computeBodyPosition(boundary)
          // println(s"PositionDone: Body ${body.id} Position ${body.position} Velocity ${body.velocity}")
          ref ! PositionDoneResponse(body)
          Behaviors.same
        case _ => throw new IllegalStateException()
    }
    
    

