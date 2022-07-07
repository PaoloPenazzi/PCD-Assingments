package distributed

import akka.actor.typed.*
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.*
import akka.actor.typed.scaladsl.adapter.*
import akka.pattern.ask
import akka.util.Timeout
import distributed.SensorState.SensorState
import distributed.{CityGrid, Message, ViewCommand}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Random, Success}

sealed trait SensorCommand extends Message

case class Update() extends SensorCommand
case class EndDisconnection() extends SensorCommand
case class ReconnectToGUI() extends SensorCommand
case class IsSensorInAlarmRequest(replyTo: ActorRef[SensorCommand]) extends SensorCommand
case class IsSensorInAlarmResponse(sensorState: SensorState) extends SensorCommand
case class ViewRegistered(views: List[ActorRef[ViewCommand]]) extends SensorCommand
case class FireStationRegistered(fireStation: List[ActorRef[FireStationCommand]]) extends SensorCommand
case class OtherSensorRegistered(otherSensors: List[ActorRef[SensorCommand]]) extends SensorCommand
case class MyZoneSensorRequest(sensorToReply: ActorRef[SensorCommand], zone: String) extends SensorCommand
case class MyZoneSensorResponse(sensorRef: ActorRef[SensorCommand]) extends SensorCommand
case class MyStationResponse(ref: ActorRef[FireStationCommand]) extends SensorCommand
case class GetSensorInfo(ctx: ActorRef[ViewCommand]) extends SensorCommand

object SensorState extends Enumeration {
  type SensorState = Value
  val OK, Warning, Disconnected = Value
}

object SensorActor:
  val sensorKey: ServiceKey[SensorCommand] = ServiceKey[SensorCommand]("sensor")
  var viewActors: ListBuffer[ActorRef[ViewCommand]] = ListBuffer.empty
  
  def sensorRead: Double = Random.between(0.0, 10.5)

  def apply(position: (Int, Int),
            zone: String,
            fireStation: Option[ActorRef[FireStationCommand]] = None): Behavior[SensorCommand] =
    Behaviors.setup(context => {
      context.system.receptionist ! Receptionist.Register(sensorKey, context.self)
      context.spawnAnonymous(manageViewActor(context.self))
      context.spawnAnonymous(manageFireStation(context.self))
      context.spawnAnonymous(manageOtherSensor(context.self))
      Behaviors.withTimers(timer => {
        sensorLogic(position, zone, context, timer, fireStation)
      })
    })

  private def manageViewActor(sendReplyTo: ActorRef[SensorCommand]): Behavior[Receptionist.Listing] =
    Behaviors.setup(context => {
      context.system.receptionist ! Receptionist.Subscribe(ViewActor.viewKey, context.self)
      Behaviors.receiveMessage {
        case msg: Receptionist.Listing =>
          sendReplyTo ! ViewRegistered(msg.serviceInstances(ViewActor.viewKey).toList)
          Behaviors.same
      }
    })

  private def manageFireStation(sendReplyTo: ActorRef[SensorCommand]): Behavior[Receptionist.Listing] =
    Behaviors.setup(context => {
      context.system.receptionist ! Receptionist.Subscribe(FireStationActor.fireStationKey, context.self)
      Behaviors.receiveMessage {
        case msg: Receptionist.Listing =>
          sendReplyTo ! FireStationRegistered(msg.serviceInstances(FireStationActor.fireStationKey).toList)
          Behaviors.same
      }
    })

  private def manageOtherSensor(sendReplyTo: ActorRef[SensorCommand]): Behavior[Receptionist.Listing] =
    Behaviors.setup(context => {
      context.system.receptionist ! Receptionist.Subscribe(SensorActor.sensorKey, context.self)
      Behaviors.receiveMessage {
        case msg: Receptionist.Listing =>
          sendReplyTo ! OtherSensorRegistered(msg.serviceInstances(SensorActor.sensorKey).toList)
          Behaviors.same
      }
    })

  private def sensorLogic(position: (Int, Int),
                          zone: String,
                          ctx: ActorContext[SensorCommand],
                          timer: TimerScheduler[SensorCommand],
                          fireStation: Option[ActorRef[FireStationCommand]] = None,
                          otherSensor: ListBuffer[ActorRef[SensorCommand]] = ListBuffer.empty): Behavior[SensorCommand] =
    implicit val timeout: Timeout = 2.seconds
    var level = 0.0
    var sensorResponse = ListBuffer.empty[SensorState]
    Behaviors.receiveMessage(msg => {
      msg match
        case ViewRegistered(views) =>
          views.filter(!viewActors.contains(_)).foreach(viewActors += _)
          Behaviors.same

        case OtherSensorRegistered(other) =>
          other.filter(!otherSensor.contains(_)).foreach(_ ! MyZoneSensorRequest(ctx.self, zone))
          Behaviors.same

        case MyZoneSensorRequest(sensorToReply, zn) =>
          if (zone == zn) {
            sensorToReply ! MyZoneSensorResponse(ctx.self)
          }
          Behaviors.same

        case MyZoneSensorResponse(sensorRef) =>
          if !otherSensor.contains(sensorRef)
          then
            otherSensor += sensorRef
            sensorLogic(position, zone, ctx, timer, fireStation, otherSensor)
          Behaviors.same

        case FireStationRegistered(listings) =>
          if fireStation.isEmpty then listings.foreach(_ ! MyZoneRequest(ctx.self, zone))
          Behaviors.same

        case MyStationResponse(fireStationRef) =>
          sensorLogic(position, zone, ctx, timer, Some(fireStationRef), otherSensor)

        case Update() =>
          level = sensorRead
          level match

            case level if level <= 8 =>
              println("Sensor" + zone + " - OK " + level)
              viewActors.foreach(_ ! SensorUpdate(position, false))
              timer.startSingleTimer(Update(), 20.seconds)
              Behaviors.same

            case level if level <= 10 =>
              viewActors.foreach(_ ! SensorUpdate(position, true))
              println("Sensor" + zone + " - WARNING")
              timer.startSingleTimer(Update(), 20.seconds)
              otherSensor.foreach(actor => ctx.ask(actor, IsSensorInAlarmRequest.apply) {
                case Success(IsSensorInAlarmResponse(sensorState)) => IsSensorInAlarmResponse(sensorState)
                case _ => IsSensorInAlarmResponse(SensorState.Disconnected)
              })
              Behaviors.same

            case _ =>
              viewActors.foreach(_ ! SensorDisconnected(position))
              println("Sensor" + zone + " - DISCONNECTED")
              disconnectedSensorLogic(position, zone, ctx, timer, fireStation, otherSensor)

        case IsSensorInAlarmRequest(replyTo) =>
          val myLevel: SensorState = if level <= 8 then SensorState.OK else SensorState.Warning
          replyTo ! IsSensorInAlarmResponse(myLevel)
          Behaviors.same

        case IsSensorInAlarmResponse(sensorState) =>
          sensorResponse += sensorState
          if sensorResponse.size == otherSensor.size
          then
            val quorum: Double = sensorResponse.count(_ == SensorState.Warning) / sensorResponse.size.toDouble
            if quorum > 0.5
            then
              fireStation.get ! Alarm(zone)
              viewActors.foreach(_ ! AlarmView(zone))
            sensorResponse = ListBuffer.empty
          Behaviors.same

        case ReconnectToGUI() =>
          println("Sensor" + zone + " - RECONNECTED")
          viewActors.foreach(_ ! SensorReconnected(position))
          ctx.self ! Update()
          Behaviors.same

        case GetSensorInfo(context) =>
          if !viewActors.contains(context) then viewActors += context
          context ! SensorInfo(position)
          timer.startSingleTimer(Update(), 10.seconds)
          Behaviors.same

        case EndDisconnection() => Behaviors.same
    })

  private def disconnectedSensorLogic(position: (Int, Int),
                                      zone: String,
                                      ctx: ActorContext[SensorCommand],
                                      timer: TimerScheduler[SensorCommand],
                                      fireStation: Option[ActorRef[FireStationCommand]] = None,
                                      otherSensor: ListBuffer[ActorRef[SensorCommand]] = ListBuffer.empty): Behavior[SensorCommand] =
    timer.startSingleTimer(EndDisconnection(), Random.between(10, 30).seconds)
    Behaviors.receiveMessage(msg => {
      msg match

        case GetSensorInfo(context) =>
          if !viewActors.contains(context) then viewActors += context
          context ! SensorInfo(position)
          Behaviors.same

        case ViewRegistered(views) =>
          views.filter(!viewActors.contains(_)).foreach(viewActors += _)
          Behaviors.same

        case EndDisconnection() =>
          ctx.self ! ReconnectToGUI()
          sensorLogic(position, zone, ctx, timer, fireStation, otherSensor)

        case _ => Behaviors.same
    })
