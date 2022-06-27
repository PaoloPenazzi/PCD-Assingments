package distributed

import distributed.CityGrid
import distributed.Message
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.*
import akka.actor.typed.scaladsl.adapter.*
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.receptionist.ServiceKey
import scala.util.Random
import concurrent.duration.DurationInt

// al momento con un sensore per zona questo invia solo messaggi all'esterno sul suo livello
// in futuro (con 3 sensori) questo dovrà inviare messaggi di sincronizzazione anche al resto dei sensori
sealed trait SensorCommand extends Message
case class Update() extends SensorCommand


object SensorActor:
  def sensorRead: Double = Random.between(0.0, 10.0)

  def apply(position: (Int, Int),
            fireStation: Option[ActorRef[FireStationCommand]] = None): Behavior[SensorCommand|Receptionist.Listing] =
    Behaviors.setup[SensorCommand | Receptionist.Listing]( ctx => {
      ctx.system.receptionist ! Receptionist.Subscribe(ServiceKey[FireStationCommand]("fireStationTODO"), ctx.self)
      Behaviors.withTimers( timers => {
        Behaviors.receiveMessage(msg => {
          msg match
            case msg:Receptionist.Listing =>
              println(s"New Firestation! $msg")
              fireStation = Some(msg.serviceInstances(FireStation.fireStationServiceKey).head)

            case Update() =>
              println("Update sensor")
              sensorRead match
                // se update > 7 --> errore
                case _ > 7 => ???
                // recepionist allarme a chi gestisce il messaggio allarme
                // se update < 7 --> tutto regolare
                case _ =>
                  println("Tutto regolare Update")
                  timers.startSingleTimer(Update(), 5000.millis)
                  Behaviors.same
            // caso di errore futuro da gestire...
            case other => Behaviors.stopped
        })
      })

    })

@main
def createSensors(): Unit =
  val cityGrid = CityGrid(300, 300)
  cityGrid.createCityGrid(3, 3)
  cityGrid.zones.foreach(z => SensorActor(new Sensor(Random.between(z.bounds.x0.toInt, z.bounds.x1.toInt),
    Random.between(z.bounds.y0.toInt, z.bounds.y1.toInt))))


