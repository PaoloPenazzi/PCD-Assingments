package distributed

import distributed.CityGrid
import distributed.Message
import distributed.ViewCommand
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.*
import akka.actor.typed.scaladsl.adapter.*
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.ServiceKey
import scala.util.Random
import concurrent.duration.DurationInt

sealed trait SensorCommand extends Message
case class Update() extends SensorCommand
case class GetInfoSensor(ctx: ActorRef[ViewCommand | Receptionist.Listing]) extends SensorCommand


object SensorActor:
  def sensorRead: Double = Random.between(0.0, 10.5)
  var viewActor: Option[ActorRef[ViewCommand | Receptionist.Listing]] = None

  def apply(position: (Int, Int),
            id: String,
            zone: String): Behavior[SensorCommand|Receptionist.Listing] =
    Behaviors.setup[SensorCommand | Receptionist.Listing]( ctx => {
      ctx.system.receptionist ! Receptionist.Subscribe(ServiceKey[Message]("Station" + zone), ctx.self)
      var fireStation: Option[ActorRef[Message]] = None
      Behaviors.withTimers( timers => {
        Behaviors.receiveMessage(msg => {
          msg match
            case msg:Receptionist.Listing =>
              println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + msg)
              println("ZOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOONEEEE" + zone)
              println(msg.serviceInstances(ServiceKey[Message]("Station" + zone)).size)
              if msg.serviceInstances(ServiceKey[Message]("Station" + zone)).nonEmpty then
                fireStation = Some(msg.serviceInstances(ServiceKey[Message]("Station" + zone)).head)
                Behaviors.same
              else Behaviors.same
            case Update() =>
              val level: Double = sensorRead
              level match
                case level if level <= 7 =>
                  println(id + ": OK")
                  timers.startSingleTimer(Update(), 5000.millis)
                  Behaviors.same
                case level if level <= 10 =>
                  println(id + ": ALARM")
                  // TODO avvisare gli altri sensori
                  fireStation.get ! Alarm(id)
                  viewActor.get ! AlarmView(id)
                  Behaviors.same
                case _ =>
                  Thread.sleep(15000)
                  Behaviors.same
            case GetInfoSensor(ctx) =>
              viewActor = Some(ctx)
              ctx ! SensorInfo(position)
              Behaviors.same
            case _ =>
              throw new IllegalStateException()
              Behaviors.same
        })
      })
    })


