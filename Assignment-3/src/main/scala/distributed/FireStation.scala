package distributed

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import distributed.Message
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import distributed.Message

sealed trait FireStationCommand extends Message
case class Alarm() extends FireStationCommand
case class StartAssistance() extends FireStationCommand
case class EndAssistance() extends FireStationCommand
case class GetInfo() extends FireStationCommand
case class sensorInAlarm() extends FireStationCommand

object FireStationActor:
  enum Status:
    case Busy
    case Normal

  val status: Status = Status.Normal

  def apply(position: (Int, Int),
            id: String,
            zone: String): Behavior[FireStationCommand] = Behaviors.setup(ctx => {
    ctx.system.receptionist ! Receptionist.Register(ServiceKey[FireStationCommand](id), ctx.self)
    Behaviors.withTimers(timers => {
      Behaviors.receiveMessage(msg => {
        msg match
          case GetInfo() =>
          case getInfo(ctx) =>
            ctx ! StationInfo(position)
            Behaviors.same
          case Alarm() =>
            println(id + ": Alarm Received")
            timers.startSingleTimer(StartAssistance(), 3000.millis)
            Behaviors.same
          case StartAssistance() =>
            println(id + ": Assistance Started")
            status = Status.Busy
            // TODO notify GUI
            busyBehavior
          case _ => ???
      })
    })
  })

  def busyBehavior: Behavior[FireStationCommand] = Behaviors.receive((ctx, msg) => {
    msg match
      case EndAssistance() =>
        //println(id + ": Assistance Started")
        status = Status.Busy
        // TODO finish behavior
      case _ => ???
  })


