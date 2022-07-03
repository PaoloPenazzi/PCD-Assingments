package distributed

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import distributed.Message
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import distributed.ViewCommand

import concurrent.duration.DurationInt
import scala.collection.mutable.ListBuffer

sealed trait FireStationCommand extends Message
case class Alarm(zone: String) extends FireStationCommand
case class StartAssistance() extends FireStationCommand
case class EndAssistance() extends FireStationCommand
case class NewViewRegistered(views: List[ActorRef[ViewCommand | Receptionist.Listing]]) extends FireStationCommand
case class MyZoneRequest(replyTo: ActorRef[SensorCommand], zone: String) extends FireStationCommand
case class GetStationInfo(ctx: ActorRef[ViewCommand | Receptionist.Listing]) extends FireStationCommand
case class sensorInAlarm() extends FireStationCommand

object FireStationActor:
  val fireStationKey: ServiceKey[FireStationCommand] = ServiceKey[FireStationCommand]("fireStation")
  var viewActors: ListBuffer[ActorRef[ViewCommand | Receptionist.Listing]] = ListBuffer.empty

  def apply(position: (Int, Int), zone: String): Behavior[FireStationCommand] =
    Behaviors.setup(ctx => {
      ctx.system.receptionist ! Receptionist.Register(fireStationKey, ctx.self)
      ctx.spawnAnonymous(manageViewActor(ctx.self))
      standardBehavior(position, zone, ctx)
  })

  private def manageViewActor(sendReplyTo: ActorRef[FireStationCommand]): Behavior[Receptionist.Listing] =
    Behaviors.setup (context => {
      context.system.receptionist ! Receptionist.Subscribe(ViewActor.sensorKey, context.self)
      Behaviors.receiveMessage {
        case msg: Receptionist.Listing =>
          sendReplyTo ! NewViewRegistered(msg.serviceInstances(ViewActor.sensorKey).toList)
          Behaviors.same
      }
    })

  def standardBehavior(position: (Int, Int),
                       zone: String,
                       ctx: ActorContext[FireStationCommand]): Behavior[FireStationCommand] =
    var alarmReceived = false

    Behaviors.withTimers(timers => {
      Behaviors.receiveMessage(msg => {
        msg match

          case GetStationInfo(viewActorRef) =>
            viewActors += viewActorRef
            viewActorRef ! StationInfo(position, ctx.self, zone)
            //viewActors.get ! StationInfo(position, ctx.self, zone)
            Behaviors.same

          case NewViewRegistered(views) =>
            views.filter(!viewActors.contains(_)).foreach(viewActors += _)
            viewActors.foreach( _ ! StationInfo(position, ctx.self, zone))
            Behaviors.same

          case MyZoneRequest(reply, zn) =>
            if zone == zn then
              reply ! MyStationResponse(ctx.self)
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
            viewActors.foreach(_ ! StationBusy(position))
            //viewActors.get ! StationBusy(position)
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

      case GetStationInfo(viewActorRef) =>
        viewActors += viewActorRef
        viewActorRef ! StationInfo(position, context.self, zone)
        //viewActors.get ! StationInfo(position, ctx.self, zone)
        Behaviors.same

      case NewViewRegistered(views) =>
        views.filter(!viewActors.contains(_)).foreach(viewActors += _)
        viewActors.foreach( _ ! StationInfo(position, context.self, zone))
        Behaviors.same
      
      case EndAssistance() =>
        //println("Station" + zone + ": Assistance Ended")
        standardBehavior(position, zone, context)
        
      case _ => throw IllegalStateException()
  })


