package distributed

import distributed.CityGrid
import distributed.Message
import distributed.ViewCommand
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.*
import akka.actor.typed.scaladsl.adapter.*
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.ServiceKey
import scala.util.Random
import concurrent.duration.DurationInt

sealed trait SensorCommand extends Message
case class Update() extends SensorCommand
case class ReconnectToGUI() extends SensorCommand
case class FirestationRegistered(firestation: List[ActorRef[FireStationCommand]]) extends SensorCommand
case class MyStationResponse(ref: ActorRef[FireStationCommand]) extends SensorCommand
case class GetSensorInfo(ctx: ActorRef[ViewCommand | Receptionist.Listing]) extends SensorCommand

object SensorActor:
  // qua ci vanno le variabile che sono UNICHE per tutti i sensori
  val sensorKey: ServiceKey[SensorCommand] = ServiceKey[SensorCommand]("sensor")
  var viewActor: Option[ActorRef[ViewCommand | Receptionist.Listing]] = None

  def sensorRead: Double = Random.between(0.0, 10.5)

  def apply(position: (Int, Int), zone: String, fireStation: Option[ActorRef[FireStationCommand]] = None): Behavior[SensorCommand] =
    Behaviors.setup (context => {
      println("SensorActor created with zone: " + zone)
      context.spawnAnonymous(manageFirestation(context.self))
      context.system.receptionist ! Receptionist.Register(sensorKey, context.self)
      Behaviors.withTimers(timer => {
        sensorLogic(position, zone, context, timer, fireStation)
      })
    })

  def manageFirestation(sendReplyTo: ActorRef[SensorCommand]): Behavior[Receptionist.Listing] =
    Behaviors.setup (context => {
      context.system.receptionist ! Receptionist.Subscribe(FireStationActor.fireStationKey, context.self)
      Behaviors.receiveMessage {
        case msg: Receptionist.Listing =>
          sendReplyTo ! FirestationRegistered(msg.serviceInstances(FireStationActor.fireStationKey).toList)
          Behaviors.same
      }
    })

  def sensorLogic(position: (Int, Int),
                  zone: String,
                  ctx: ActorContext[SensorCommand],
                  timer: TimerScheduler[SensorCommand],
                  fireStation: Option[ActorRef[FireStationCommand]] = None): Behavior[SensorCommand] =
    Behaviors.receiveMessage(msg => {
      msg match
        case FirestationRegistered(listings) =>
          if fireStation.isEmpty then listings.foreach(act => act ! MyZoneRequest(ctx.self, zone))
          Behaviors.same
        case MyStationResponse(fireStationRef) =>
          println("I'm Sensor: "+ zone+ " My Firestation has actorRef: "+fireStationRef)
          sensorLogic(position, zone, ctx, timer, Some(fireStationRef))
        case Update() =>
          val level: Double = sensorRead
          level match
            case level if level <= 8 =>
              println("Sensor" + zone + " - OK(" + level + ")")
              viewActor.get ! SensorUpdate(position, false)
              timer.startSingleTimer(Update(), 10000.millis)
              Behaviors.same
            case level if level <= 10 =>
              println("Sensor" + zone + " - WARNING(" + level + ")")
              viewActor.get ! SensorUpdate(position, true)
              // TODO avvisare gli altri sensori
              println("Sensor" + zone + " - Send Alarm to Firestation: " + fireStation.get)
              fireStation.get ! Alarm(zone)
              viewActor.get ! AlarmView(zone)
              timer.startSingleTimer(Update(), 10000.millis)
              Behaviors.same
            case _ =>
              println("Sensor" + zone + " - DISCONNECTED")
              viewActor.get ! SensorDisconnected(position)
              timer.startSingleTimer(ReconnectToGUI(), 20000.millis)
              Behaviors.same
        case ReconnectToGUI() =>
          println("Sensor" + zone + " - RECONNECTED")
          viewActor.get ! SensorReconnected(position)
          Behaviors.same
        case GetSensorInfo(context) =>
          viewActor = Some(context)
          context ! SensorInfo(position)
          ctx.self ! Update()
          Behaviors.same
    })