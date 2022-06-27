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

object FireStationActor :

  enum Status:
    case Busy
    case Normal

  val fireStationServiceKey = ServiceKey[FireStationCommand]("fireStationTODO")

  val status: Status = Status.Normal

  def apply(): Behavior[FireStationCommand] = Behaviors.setup( ctx => {
    ctx.system.receptionist ! Receptionist.Register(fireStationServiceKey, ctx.self)

    Behaviors.receiveMessage( msg => {
      msg match
        case GetInfo() => ???
        case Alarm() => ???
        case StartAssistance() => ???
        case other => ???
    })
  })

  def busyBehavior: Behavior[FireStationCommand] = Behaviors.receive((ctx, msg) => {
    msg match
      case EndAssistance() => ???
      case other => ???
  })


