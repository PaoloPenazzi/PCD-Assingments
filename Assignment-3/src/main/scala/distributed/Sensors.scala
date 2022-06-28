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
case class MyZoneResponse(ref: ActorRef[FireStationCommand]) extends SensorCommand
case class GetInfoSensor(ctx: ActorRef[ViewCommand | Receptionist.Listing]) extends SensorCommand

object SensorActor:
  val sensorKey: ServiceKey[SensorCommand] = ServiceKey[SensorCommand]("sensor")
  var viewActor: Option[ActorRef[ViewCommand | Receptionist.Listing]] = None
  var fireStation: Option[ActorRef[FireStationCommand]] = None

  def sensorRead: Double = Random.between(0.0, 10.5)

  def apply(position: (Int, Int), zone: String): Behavior[SensorCommand | Receptionist.Listing] =
    Behaviors.setup (ctx => {
      ctx.system.receptionist ! Receptionist.Register(sensorKey, ctx.self)
      ctx.system.receptionist ! Receptionist.Subscribe(FireStationActor.fireStationKey, ctx.self)
      Behaviors.withTimers( timer => {
        sensorLogic(position, zone, ctx, timer)
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
        case msg: Receptionist.Listing =>
          if msg.serviceInstances(FireStationActor.fireStationKey).nonEmpty
          then
            println(msg)
            msg.serviceInstances(FireStationActor.fireStationKey).foreach(act => act ! MyZoneRequest(ctx.self, zone) )
            Behaviors.same
          else
            println("EMPTY")
            Behaviors.same
        case Update() =>
          val level: Double = sensorRead
          level match
            case level if level <= 7 =>
              println(zone + ": OK")
              timer.startSingleTimer(Update(), 5000.millis)
              Behaviors.same
            case level if level <= 10 =>
              println(zone + ": ALARM")
              // TODO avvisare gli altri sensori
              fireStation.get ! Alarm(zone)
              viewActor.get ! AlarmView(zone)
              Behaviors.same
            case _ =>
              Thread.sleep(15000)
              Behaviors.same
        case GetInfoSensor(context) =>
          viewActor = Some(context)
          context ! SensorInfo(position)
          Behaviors.same
    })