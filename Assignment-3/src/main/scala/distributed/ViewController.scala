package distributed

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import distributed.Message
import distributed.Zone

trait ViewCommand extends Message
case class StartGUI(cityGrid: CityGrid) extends ViewCommand
case class SensorDisconnected(position: (Int, Int)) extends ViewCommand
case class SensorReconnected(position: (Int, Int)) extends ViewCommand
case class SensorInfo(position: (Int, Int)) extends ViewCommand
case class StationInfo(position: (Int, Int)) extends ViewCommand
case class StationOccupied(position: (Int, Int)) extends ViewCommand
case class StationFree(position: (Int, Int)) extends ViewCommand
case class AlarmView(id: String) extends ViewCommand
case class SensorUpdate(position: (Int, Int), overLevel: Boolean) extends ViewCommand

object ViewActor:
  var view: Option[View] = None
  var city: Option[CityGrid] = None

  def refreshGUI(): Unit = view.get.display(city.get)

  def apply(): Behavior[ViewCommand | Receptionist.Listing] =
    Behaviors.setup(ctx => {
      Behaviors.receiveMessage { message =>
        message match
          case message: Receptionist.Listing =>
            val id = message.getKey.id
            id match
              case id if id.contains("sensor") =>
                println(message)
                message.serviceInstances(SensorActor.sensorKey).toList.foreach(z => z ! GetInfoSensor(ctx.self))
                Behaviors.same
              case id if id.contains("fire") =>
                println(message)
                message.serviceInstances(FireStationActor.fireStationKey).toList.foreach(z => z ! GetInfoStation(ctx.self))
                Behaviors.same

          case StartGUI(cityGrid) =>
            view = Some(View(cityGrid.width + 100, cityGrid.height + 100))
            city = Some(cityGrid)
            view.get.start()
            refreshGUI()
            ctx.system.receptionist ! Receptionist.Subscribe(SensorActor.sensorKey, ctx.self)
            ctx.system.receptionist ! Receptionist.Subscribe(FireStationActor.fireStationKey, ctx.self)
            Behaviors.same

          case SensorInfo(position) =>
            if city.get.sensors.contains(position)
            then
              Behaviors.same
            else
              city.get.sensors = city.get.sensors + (position -> false)
              refreshGUI()
              Behaviors.same

          case StationInfo(position) =>
            if city.get.fireStations.contains(position)
            then
              Behaviors.same
            else
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

          case StationOccupied(position) =>
            city.get.fireStations = city.get.fireStations + (position -> true)
            refreshGUI()
            Behaviors.same

          case StationFree(position) =>
            city.get.fireStations = city.get.fireStations + (position -> false)
            refreshGUI()
            Behaviors.same

          case AlarmView(id) =>
            println("Alarm Received")
            val zoneAlarmed = city.get.zones.find(z => z.id == id)
            city.get.zonesAlarmed = city.get.zonesAlarmed :+ (zoneAlarmed.get)
            refreshGUI()
            Behaviors.same

          case SensorUpdate(position, overLevel) =>
            if overLevel 
            then city.get.sensors = city.get.sensors + (position -> true)
            else city.get.sensors = city.get.sensors + (position -> false)
            refreshGUI()
            Behaviors.same

          case _ => throw IllegalStateException()
      }
    })
