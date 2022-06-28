package distributed

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import distributed.Message
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.{Behaviors, ActorContext}
import distributed.ViewCommand
import concurrent.duration.DurationInt

sealed trait FireStationCommand extends Message
case class Alarm(id: String) extends FireStationCommand
case class StartAssistance() extends FireStationCommand
case class EndAssistance() extends FireStationCommand
case class GetInfoStation(ctx: ActorRef[ViewCommand | Receptionist.Listing]) extends FireStationCommand
case class sensorInAlarm() extends FireStationCommand

object FireStationActor:
  enum Status:
    case Busy
    case Normal

  var viewActor: Option[ActorRef[ViewCommand | Receptionist.Listing]] = None
  var status: Status = Status.Normal

  def apply(position: (Int, Int),
            id: String): Behavior[FireStationCommand] = Behaviors.setup(ctx => {
    ctx.system.receptionist ! Receptionist.Register(ServiceKey[FireStationCommand](id), ctx.self)
    standardBehavior(position, id)
  })

  def standardBehavior(position: (Int, Int),
                       id: String): Behavior[FireStationCommand] = Behaviors.withTimers(timers => {
    Behaviors.receiveMessage(msg => {
      msg match
        case GetInfoStation(ctx) =>
          viewActor = Some(ctx)
          ctx ! StationInfo(position)
          Behaviors.same
        case Alarm(zoneId) =>
          if zoneId.equals(id)
          then
            println(id + ": Alarm Received")
            timers.startSingleTimer(StartAssistance(), 3000.millis)
          Behaviors.same
        case StartAssistance() =>
          println(id + ": Assistance Started")
          status = Status.Busy
          viewActor.get ! StationOccupied(position)
          busyBehavior(position, id)
        case _ => throw IllegalStateException()
    })
  })

  def busyBehavior(position: (Int, Int),
                   id: String): Behavior[FireStationCommand] = Behaviors.receive((ctx, msg) => {
    msg match
      case EndAssistance() =>
        status = Status.Normal
        standardBehavior(position, id)
      case _ => throw IllegalStateException()
  })


