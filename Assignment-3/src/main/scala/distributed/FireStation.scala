package distributed

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import distributed.Message
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.{Behaviors, ActorContext}
import distributed.ViewCommand
import concurrent.duration.DurationInt

sealed trait FireStationCommand extends Message
case class Alarm(zone: String) extends FireStationCommand
case class StartAssistance() extends FireStationCommand
case class EndAssistance() extends FireStationCommand
case class MyZoneRequest(replyTo: ActorRef[SensorCommand], zone: String) extends FireStationCommand
case class GetInfoStation(ctx: ActorRef[ViewCommand | Receptionist.Listing]) extends FireStationCommand
case class sensorInAlarm() extends FireStationCommand

object FireStationActor:
  val fireStationKey: ServiceKey[FireStationCommand] = ServiceKey[FireStationCommand]("fireStation")

  enum Status:
    case Busy
    case Normal

  var viewActor: Option[ActorRef[ViewCommand | Receptionist.Listing]] = None
  var status: Status = Status.Normal

  def apply(position: (Int, Int), zone: String): Behavior[FireStationCommand] =
    Behaviors.setup(ctx => {
      ctx.system.receptionist ! Receptionist.Register(fireStationKey, ctx.self)
      standardBehavior(position, zone, ctx)
  })

  def standardBehavior(position: (Int, Int), zone: String, ctx: ActorContext[FireStationCommand]): Behavior[FireStationCommand] =
    Behaviors.withTimers(timers => {
      Behaviors.receiveMessage(msg => {
        msg match

          case MyZoneRequest(reply, zn) =>
            println("my zone req received")
            if zone == zn then
              reply ! MyZoneResponse(ctx.self)
            Behaviors.same

          case GetInfoStation(ctx) =>
            viewActor = Some(ctx)
            ctx ! StationInfo(position)
            Behaviors.same

          case Alarm(zoneId) =>
            if zoneId.equals(zone)
            then
              println(zone + ": Alarm Received")
              timers.startSingleTimer(StartAssistance(), 3000.millis)
            Behaviors.same

          case StartAssistance() =>
            println(zone + ": Assistance Started")
            status = Status.Busy
            viewActor.get ! StationOccupied(position)
            busyBehavior(position, zone, ctx)

          case _ => throw IllegalStateException()
      })
  })

  def busyBehavior(position: (Int, Int),
                   zone: String,
                   ctx: ActorContext[FireStationCommand]): Behavior[FireStationCommand] = Behaviors.receive((ctx, msg) => {
    msg match
      case EndAssistance() =>
        status = Status.Normal
        standardBehavior(position, zone, ctx)
      case _ => throw IllegalStateException()
  })


