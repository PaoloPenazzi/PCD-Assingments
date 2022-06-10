import akka.actor.typed.{ActorRef, ActorSystem, Behavior, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.mutable
import scala.util.Random

object SimulationActor:
  var responseCounter = 0
  var actorsList: mutable.Seq[ActorRef[Command]] = mutable.Seq.empty
  var view: ActorRef[Command] = null

  def apply(simulation: Simulation, gui: Boolean): Behavior[Command] =
    Behaviors.receive { (context, msg) =>
      msg match
        case Command.StopSimulation =>
          // come faccio in modo di gestire tutti i messaggi che ancora mi devono arrivare? Li lascio li e li faccio ripartire
          // oppure faccio finire gli ultimi calcoli e poi blocco tutto?
          ???
          
        case Command.ResumeSimulation =>
          // come faccio ripartire tutto? Da dove riprendo? Perché?
          ???
          
        case Command.StartSimulation =>
          println("Simulation Started!")
          // creo gli attori
          if gui then {view = context.spawn(ViewActor(), "view-actor"); view ! Command.StartGUI}
          for (n <- 0 until simulation.numBodies)
            val newActor = context.spawn(BodyActor(simulation.bodies(n)), "actor-number-"+ n.toString)
            actorsList = actorsList :+ newActor
            println(s"Actor: $newActor Number: $n")
            // mando i messaggi di partire a fare i calcoli
            newActor ! Command.ComputeVelocityRequest(simulation.bodies, context.self)
          Behaviors.same
          
        case Command.VelocityDoneResponse(result) =>
          println(s"VelocityDone for ${result.id}")
          simulation.bodies(result.id).velocity = result.velocity
          responseCounter = responseCounter + 1
          if (responseCounter == simulation.bodies.size)
            responseCounter = 0
            actorsList.foreach(y => y ! Command.ComputePositionRequest(simulation.boundary, context.self))
          Behaviors.same
          
        case Command.PositionDoneResponse(result) =>
          println(s"PositionDone for ${result.id}")
          simulation.bodies(result.id).position = result.position
          responseCounter = responseCounter + 1
          if (responseCounter == simulation.bodies.size)
            responseCounter = 0
            view ! Command.UpdateGUI(simulation.bodies, 0.001, simulation.iteration, simulation.boundary)
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
    
    

