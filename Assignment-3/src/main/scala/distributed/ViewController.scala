package distributed

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import distributed.Message
import distributed.Zone

import javax.swing.SwingUtilities

trait ViewCommand extends Message
case class StartGUI(cityGrid: CityGrid) extends ViewCommand
case class SensorDisconnected(position: (Int, Int)) extends ViewCommand
case class SensorReconnected(position: (Int, Int)) extends ViewCommand
case class SensorInfo(position: (Int, Int)) extends ViewCommand
case class StationInfo(position: (Int, Int), actorRef: ActorRef[FireStationCommand], zoneID: String) extends ViewCommand
case class StationBusy(position: (Int, Int)) extends ViewCommand
case class StationFree(position: (Int, Int)) extends ViewCommand
case class AlarmView(id: String) extends ViewCommand
case class ResetAlarm(id: String) extends ViewCommand
case class SensorUpdate(position: (Int, Int), overLevel: Boolean) extends ViewCommand

object ViewActor:
  var view: Option[View] = None
  var city: Option[CityGrid] = None
  var fireStationActors: Map[String, ActorRef[FireStationCommand]] = Map.empty
  var fireStationZone: Map[String, (Int, Int)] = Map.empty


  def refreshGUI(): Unit =
    SwingUtilities.invokeLater(() => {
      view.get.display(city.get)
    })

  def apply(): Behavior[ViewCommand | Receptionist.Listing] =
    Behaviors.setup(ctx => {
      Behaviors.receiveMessage { message =>
        message match

          case message: Receptionist.Listing =>
            val id = message.getKey.id
            id match
              case id if id.contains("sensor") =>
                message.serviceInstances(SensorActor.sensorKey).toList.foreach(_ ! GetSensorInfo(ctx.self))
                Behaviors.same
              case id if id.contains("fire") =>
                message.serviceInstances(FireStationActor.fireStationKey).toList.foreach(_ ! GetStationInfo(ctx.self))
                Behaviors.same

          case StartGUI(cityGrid) =>
            view = Some(View(cityGrid.width + 600, cityGrid.height + 200, ctx.self))
            city = Some(cityGrid)
            view.get.start(cityGrid.zones)
            ctx.system.receptionist ! Receptionist.Subscribe(SensorActor.sensorKey, ctx.self)
            ctx.system.receptionist ! Receptionist.Subscribe(FireStationActor.fireStationKey, ctx.self)
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
