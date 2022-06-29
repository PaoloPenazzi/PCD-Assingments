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
case class MyZoneResponse(ref: ActorRef[FireStationCommand]) extends SensorCommand
case class GetInfoSensor(ctx: ActorRef[ViewCommand | Receptionist.Listing]) extends SensorCommand

object SensorActor:
  val sensorKey: ServiceKey[SensorCommand] = ServiceKey[SensorCommand]("sensor")
  var viewActor: Option[ActorRef[ViewCommand | Receptionist.Listing]] = None
  var fireStation: Option[ActorRef[FireStationCommand]] = None

  def sensorRead: Double = Random.between(0.0, 10.5)

  def apply(position: (Int, Int), zone: String): Behavior[SensorCommand | Receptionist.Listing] =
    Behaviors.setup(context => {
      context.system.receptionist ! Receptionist.Register(sensorKey, context.self)
      context.system.receptionist ! Receptionist.Subscribe(FireStationActor.fireStationKey, context.self)
      Behaviors.withTimers(timer => {
        sensorLogic(position, zone, context, timer)
      })
    })

  def sensorLogic(position: (Int, Int),
                  zone: String,
                  ctx: ActorContext[SensorCommand | Receptionist.Listing],
                  timer: TimerScheduler[SensorCommand | Receptionist.Listing]): Behavior[SensorCommand | Receptionist.Listing] =
    Behaviors.receiveMessage(msg => {
      msg match
        case MyZoneResponse(ref) =>
          fireStation = Some(ref)
          Behaviors.same
        case message: Receptionist.Listing =>
          message.serviceInstances(FireStationActor.fireStationKey).toList.foreach(act => act ! MyZoneRequest(ctx.self, zone))
          Behaviors.same
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
              // fireStation.get ! Alarm(zone)
              viewActor.get ! AlarmView(zone)
              timer.startSingleTimer(Update(), 10000.millis)
              Behaviors.same
            case _ =>
              println("Sensor" + zone + " - FAILED")
              viewActor.get ! SensorDisconnected(position)
              timer.startSingleTimer(ReconnectToGUI(), 20000.millis)
              Behaviors.same
        case ReconnectToGUI() =>
          viewActor.get ! SensorReconnected(position)
          Behaviors.same
        case GetInfoSensor(context) =>
          viewActor = Some(context)
          context ! SensorInfo(position)
          ctx.self ! Update()
          Behaviors.same
    })