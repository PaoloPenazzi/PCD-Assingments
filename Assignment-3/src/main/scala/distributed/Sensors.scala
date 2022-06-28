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
case class GetInfoSensor(ctx: ActorRef[ViewCommand | Receptionist.Listing]) extends SensorCommand

object SensorActor:
  def sensorRead: Double = Random.between(0.0, 10.5)
  var fireStationServiceKey : Option[ServiceKey[FireStationCommand]] = None
  var viewActor: Option[ActorRef[ViewCommand | Receptionist.Listing]] = None
  var fireStation: Option[ActorRef[FireStationCommand]] = None

  def apply(position: (Int, Int), id: String, zone: String): Behavior[SensorCommand | Receptionist.Listing] =
    Behaviors.setup[SensorCommand | Receptionist.Listing] (ctx => {
      fireStationServiceKey = Some(ServiceKey[FireStationCommand]("Station" + zone))
      ctx.system.receptionist ! Receptionist.Subscribe(fireStationServiceKey.get, ctx.self)
      ctx.system.receptionist ! Receptionist.Register(ServiceKey[SensorCommand](id), ctx.self)
      Behaviors.withTimers( timer => {
        sensorLogic(position, id, zone, ctx, timer)
      })
    })

  def sensorLogic(position: (Int, Int),
                  id: String,
                  zone: String,
                  ctx: ActorContext[SensorCommand | Receptionist.Listing],
                  timer: TimerScheduler[SensorCommand | Receptionist.Listing]): Behavior[SensorCommand | Receptionist.Listing] =
    Behaviors.receiveMessage(msg => {
      msg match
        case msg: Receptionist.Listing =>
          if msg.serviceInstances(fireStationServiceKey.get).nonEmpty
          then
            fireStation = Some(msg.serviceInstances(fireStationServiceKey.get).head)
            Behaviors.same
          else
            Behaviors.same
        case Update() =>
          val level: Double = sensorRead
          level match
            case level if level <= 7 =>
              println(id + ": OK")
              timer.startSingleTimer(Update(), 5000.millis)
              Behaviors.same
            case level if level <= 10 =>
              println(id + ": ALARM")
              // TODO avvisare gli altri sensori
              fireStation.get ! Alarm(id)
              viewActor.get ! AlarmView(id)
              Behaviors.same
            case _ =>
              Thread.sleep(15000)
              Behaviors.same
        case GetInfoSensor(context) =>
          println(id + ": INFO SENSOR")
          viewActor = Some(context)
          context ! SensorInfo(position)
          Behaviors.same
    })