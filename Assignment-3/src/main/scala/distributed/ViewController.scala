package distributed

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import distributed.Message
import distributed.Zone

trait ViewCommand extends Message
case class StartGUI(cityGrid: CityGrid) extends ViewCommand
case class SensorInfo(position: (Int, Int)) extends ViewCommand
case class StationInfo(position: (Int, Int)) extends ViewCommand
case class StationOccupied(position: (Int, Int)) extends ViewCommand
case class AlarmView(id: String) extends ViewCommand
case class SensorUpdate(position: (Int, Int)) extends ViewCommand

object ViewActor:
  var view: Option[View] = None
  var city: Option[CityGrid] = None

  def apply(): Behavior[ViewCommand | Receptionist.Listing] =
    Behaviors.setup[ViewCommand | Receptionist.Listing] { ctx =>
      Behaviors.receiveMessage { message =>
        message match
          case message: Receptionist.Listing =>
            val id = message.getKey.id
            id match
              case id if id.contains("Sensor") =>
                message.serviceInstances(ServiceKey[SensorCommand](id)).toList.foreach(z => z ! GetInfoSensor(ctx.self))
                Behaviors.same
              case id if id.contains("Station") =>
                message.serviceInstances(ServiceKey[FireStationCommand](id)).toList.foreach(z => z ! GetInfoStation(ctx.self))
                Behaviors.same
          case StartGUI(cityGrid) =>
            view = Some(View(cityGrid.width + 100, cityGrid.height + 100))
            city = Some(cityGrid)
            view.get.display(city.get)
            for z <- city.get.zones
              do
                ctx.system.receptionist ! Receptionist.Subscribe(ServiceKey[FireStationCommand]("Station" + z.id), ctx.self)
                ctx.system.receptionist ! Receptionist.Subscribe(ServiceKey[SensorCommand]("Sensor" + z.id), ctx.self)
            Behaviors.same
          case SensorInfo(position) =>
            if city.get.sensors.contains(position)
            then
              Behaviors.same
            else
              city.get.sensors = city.get.sensors.+(position -> false)
              view.get.display(city.get)
              Behaviors.same
          case StationInfo(position) =>
            if city.get.fireStations.contains(position)
            then
              Behaviors.same
            else
              city.get.fireStations = city.get.fireStations.+(position -> false)
              view.get.display(city.get)
              Behaviors.same
          case StationOccupied(position) =>
            ???
            Behaviors.same
          case AlarmView(id) =>
            val zoneAlarmed = city.get.zones.find(z => z.id == id)
            city.get.zonesAlarmed = city.get.zonesAlarmed.::(zoneAlarmed.get)
            view.get.display(city.get)
            Behaviors.same
          case SensorUpdate(position: (Int, Int)) =>
            city.get.sensors = city.get.sensors + (position -> true)
            // TODO check if updates the map correctly
            view.get.display(city.get)
            Behaviors.same
          case _ => throw IllegalStateException()
      }
    }
