package distributed

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors

sealed trait ViewCommand extends Message

case class StartGUI(cityGrid: CityGrid) extends ViewCommand

case class SensorInfo(position: (Int, Int)) extends ViewCommand

case class StationInfo(position: (Int, Int)) extends ViewCommand

object ViewActor:
  var view: Option[View] = None
  var city: Option[CityGrid] = None

  def apply(): Behavior[Message] =
    Behaviors.setup[ViewCommand | Receptionist.Listing] (ctx => {
      Behaviors.receiveMessage { message =>
        message match
          case message: Receptionist.Listing => ???
            // message.allServiceInstances().foreach(z => z ! GetInfo(ctx))
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
              city.get.sensors = city.get.sensors.::(position)
              view.get.display(city.get)
              Behaviors.same
          case StationInfo(position) =>
            if city.get.fireStations.contains(position)
            then
              Behaviors.same
            else
              city.get.fireStations = city.get.fireStations.::(position)
              view.get.display(city.get)
              Behaviors.same
          case StartAssistance() =>
            Behaviors.same
          case Alarm() =>
            Behaviors.same
          case _ =>
            throw IllegalStateException()
      }
    })
