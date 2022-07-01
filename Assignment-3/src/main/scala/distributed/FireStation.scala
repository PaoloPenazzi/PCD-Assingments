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
case class GetStationInfo(ctx: ActorRef[ViewCommand | Receptionist.Listing]) extends FireStationCommand
case class sensorInAlarm() extends FireStationCommand

object FireStationActor:
  val fireStationKey: ServiceKey[FireStationCommand] = ServiceKey[FireStationCommand]("fireStation")
  var viewActor: Option[ActorRef[ViewCommand | Receptionist.Listing]] = None

  def apply(position: (Int, Int), zone: String): Behavior[FireStationCommand] =
    Behaviors.setup(ctx => {
      ctx.system.receptionist ! Receptionist.Register(fireStationKey, ctx.self)
      standardBehavior(position, zone, ctx)
  })

  def standardBehavior(position: (Int, Int),
                       zone: String,
                       ctx: ActorContext[FireStationCommand]): Behavior[FireStationCommand] =
    var alarmReceived = false

    Behaviors.withTimers(timers => {
      Behaviors.receiveMessage(msg => {
        msg match

          case MyZoneRequest(reply, zn) =>
            if zone == zn then
              reply ! MyStationResponse(ctx.self)
            Behaviors.same

          case GetStationInfo(viewActorRef) =>
            viewActor = Some(viewActorRef)
            viewActor.get ! StationInfo(position, ctx.self, zone)
            Behaviors.same

          case Alarm(zoneId) =>
            if zoneId.equals(zone) && !alarmReceived
            then
              alarmReceived = true
              println("Station" + zone + ": Alarm Received")
              timers.startSingleTimer(StartAssistance(), 3000.millis)
            Behaviors.same

          case StartAssistance() =>
            //println("Station" + zone + ": Assistance Started")
            viewActor.get ! StationBusy(position)
            busyBehavior(position, zone, ctx)

          case EndAssistance() =>
            Behaviors.same

          case _ => throw IllegalStateException()
      })
  })

  def busyBehavior(position: (Int, Int),
                   zone: String,
                   context: ActorContext[FireStationCommand]): Behavior[FireStationCommand] = Behaviors.receive((_, msg) => {
    msg match
      
      case EndAssistance() =>
        //println("Station" + zone + ": Assistance Ended")
        standardBehavior(position, zone, context)
        
      case _ => throw IllegalStateException()
  })


