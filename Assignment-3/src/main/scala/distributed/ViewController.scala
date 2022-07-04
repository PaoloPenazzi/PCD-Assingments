package distributed

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import distributed.Message
import distributed.Zone
import distributed.CityGrid
import distributed.Message
import distributed.ViewCommand
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.*
import akka.actor.typed.scaladsl.adapter.*
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, Scheduler, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.ServiceKey
import akka.util.Timeout

import javax.swing.SwingUtilities
import scala.collection.mutable.ListBuffer

trait ViewCommand extends Message
case class StartGUI(cityGrid: CityGrid) extends ViewCommand
case class SensorDisconnected(position: (Int, Int)) extends ViewCommand
case class SensorReconnected(position: (Int, Int)) extends ViewCommand
case class SensorInfo(position: (Int, Int)) extends ViewCommand
case class StartNewSensor(sensors: List[ActorRef[SensorCommand]]) extends ViewCommand
case class StartNewFireStation(firestations: List[ActorRef[FireStationCommand]]) extends ViewCommand
case class StartNewView(views: List[ActorRef[ViewCommand]]) extends ViewCommand
case class StationInfo(position: (Int, Int), actorRef: ActorRef[FireStationCommand], zoneID: String) extends ViewCommand
case class StationBusy(position: (Int, Int)) extends ViewCommand
case class StationFree(position: (Int, Int)) extends ViewCommand
case class AlarmView(id: String) extends ViewCommand
case class NotifyAlarm(id: String) extends ViewCommand
case class ResetAlarm(id: String) extends ViewCommand
case class SensorUpdate(position: (Int, Int), overLevel: Boolean) extends ViewCommand

object ViewActor:
  val viewKey: ServiceKey[ViewCommand] = ServiceKey[ViewCommand]("view")
  var view: Option[View] = None
  var city: Option[CityGrid] = None
  var fireStationActors: Map[String, ActorRef[FireStationCommand]] = Map.empty
  var fireStationZone: Map[String, (Int, Int)] = Map.empty
  var viewActors: ListBuffer[ActorRef[ViewCommand]] = ListBuffer.empty


  def refreshGUI(): Unit =
    SwingUtilities.invokeLater(() => {
      view.get.display(city.get)
    })


  private def manageFireStation(sendReplyTo: ActorRef[ViewCommand]): Behavior[Receptionist.Listing] =
    Behaviors.setup (context => {
      context.system.receptionist ! Receptionist.Subscribe(FireStationActor.fireStationKey, context.self)
      Behaviors.receiveMessage {
        case msg: Receptionist.Listing =>
          sendReplyTo ! StartNewFireStation(msg.serviceInstances(FireStationActor.fireStationKey).toList)
          Behaviors.same
      }
    })

  private def manageOtherSensor(sendReplyTo: ActorRef[ViewCommand]): Behavior[Receptionist.Listing] =
    Behaviors.setup (context => {
      context.system.receptionist ! Receptionist.Subscribe(SensorActor.sensorKey, context.self)
      Behaviors.receiveMessage {
        case msg: Receptionist.Listing =>
          sendReplyTo ! StartNewSensor(msg.serviceInstances(SensorActor.sensorKey).toList)
          Behaviors.same
      }
    })


  private def manageViewActor(sendReplyTo: ActorRef[ViewCommand]): Behavior[Receptionist.Listing] =
    Behaviors.setup (context => {
      context.system.receptionist ! Receptionist.Subscribe(viewKey, context.self)
      Behaviors.receiveMessage {
        case message: Receptionist.Listing =>
          sendReplyTo ! StartNewView(message.serviceInstances(viewKey).toList)
          Behaviors.same
      }
    })

  def apply(): Behavior[ViewCommand] =
    Behaviors.setup(ctx => {
      ctx.system.receptionist ! Receptionist.Register(viewKey, ctx.self)
      ctx.spawnAnonymous(manageViewActor(ctx.self))
      ctx.spawnAnonymous(manageOtherSensor(ctx.self))
      ctx.spawnAnonymous(manageFireStation(ctx.self))
      Behaviors.receiveMessage { message =>
        message match

          case StartNewSensor(sensors) =>
            sensors.foreach(_ ! GetSensorInfo(ctx.self))
            Behaviors.same

          case StartNewFireStation(firestations) =>
            firestations.foreach(_ ! GetStationInfo(ctx.self))
            Behaviors.same

          case StartNewView(views) =>
            views.filter(!viewActors.contains(_)).foreach(viewActors += _)
            Behaviors.same

          case StartGUI(cityGrid) =>
            view = Some(View(cityGrid.width + 600, cityGrid.height + 200, ctx.self))
            city = Some(cityGrid)
            view.get.start(cityGrid.zones)
            refreshGUI()
            Behaviors.same

          case SensorInfo(position) =>
            if !city.get.sensors.contains(position)
            then
              city.get.sensors = city.get.sensors + (position -> false)
              refreshGUI()
            Behaviors.same

          case StationInfo(position, actorRef, zoneID) =>
            if !city.get.fireStations.contains(position)
            then
              fireStationZone = fireStationZone + (zoneID -> position)
              fireStationActors = fireStationActors + (zoneID -> actorRef)
              city.get.fireStations = city.get.fireStations + (position -> false)
              refreshGUI()
            Behaviors.same

          case SensorDisconnected(position) =>
            city.get.sensorsDisconnected += position
            refreshGUI()
            Behaviors.same

          case SensorReconnected(position) =>
            city.get.sensorsDisconnected -= position
            refreshGUI()
            Behaviors.same

          case StationBusy(position) =>
            city.get.fireStations = city.get.fireStations + (position -> true)
            refreshGUI()
            Behaviors.same

          case AlarmView(zoneID) =>
            val zoneAlarmed = city.get.zones.find(_.id == zoneID).get
            if !city.get.zonesAlarmed.contains(zoneAlarmed)
            then
              city.get.zonesAlarmed += zoneAlarmed
              refreshGUI()
            Behaviors.same

          case NotifyAlarm(zoneID) =>
            viewActors.foreach( _ ! ResetAlarm(zoneID))
            Behaviors.same

          case ResetAlarm(zoneID) =>
            city.get.zonesAlarmed -= city.get.zones.find(_.id == zoneID).get
            city.get.fireStations = city.get.fireStations + (fireStationZone(zoneID) -> false)
            refreshGUI()
            fireStationActors(zoneID) ! EndAssistance()
            Behaviors.same

          case SensorUpdate(position, overLevel) =>
            city.get.sensors = city.get.sensors + (position -> overLevel)
            refreshGUI()
            Behaviors.same

          case _ => throw IllegalStateException()
      }
    })
