package distributed

import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors

sealed trait ViewCommand extends Message
case class StartGUI() extends ViewCommand
case class SensorInfo(position: (Int, Int)) extends ViewCommand
case class StationInfo(position: (Int, Int)) extends ViewCommand

object ViewActor:
  var view: View = View(600, 600)
  var city: Option[CityGrid] = None

  def apply(father: ActorRef[Message]): Behavior[Message] =
    Behaviors.receive { (_, message) =>
      message match
        case StartGUI(cityGrid) =>
          city = Some(cityGrid)
          view.display(city)
          // TODO send message to get info
          Behaviors.same
        case SensorInfo(position) =>
          city.get.sensors = city.get.sensors :: (position)
          view.display(city)
          Behaviors.same
        case StationInfo(position) =>
          city.get.fireStations = city.get.fireStations :: (position)
          view.display(city)
          Behaviors.same
        case StartAssistance() =>
          Behaviors.same
        case Alarm() =>
          Behaviors.same
        case _ =>
          throw IllegalStateException()
    }
