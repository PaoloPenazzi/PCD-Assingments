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
            fireStations: List[ActorRef[FireStationCommand]] = List.empty): Behavior[SensorCommand|Receptionist.Listing] =
    Behaviors.setup[SensorCommand | Receptionist.Listing]( ctx => {
      // operazioni preliminari per quando viene istanziato l'attore caserma
      // vanno fatte qui sotto

      ctx.system.receptionist ! Receptionist.Subscribe(ServiceKey[FireStationCommand]("fireStationTODO"), ctx.self)

      // (1) we can’t initially get a reference to the FireStation actor, so
      // declare this variable as a var field, and using Option/None
      //var fireStation: Option[ActorRef[Message]] = None

      // (2) create an ActorRef that can be thought of as a Receptionist
      // Listing “adapter.” this will be used in the next line of code.
      // the SensorActor.ListingResponse(listing) part of the code tells the
      // Receptionist how to get back in touch with us after we contact
      // it in Step 4 below.
      /*val listingAdapter: ActorRef[Receptionist.Listing] =
      ctx.messageAdapter { listing =>
        println(s"listingAdapter:listing: ${listing.toString}")
        ListingResponse(listing)
      }*/

      // (3) send a message to the Receptionist saying that we want
      // to subscribe to events related to Firestation.fireStationServiceKey, which
      // represents the Firestation actor.
      /*ctx.system.receptionist !
        Receptionist.Subscribe(FireStation.fireStationServiceKey, listingAdapter)*/
      sensorLogic(position, ctx, fireStations)
      /*Behaviors.withTimers( timers => {

        Behaviors.receiveMessage( msg => {
          msg match
            case msg:Receptionist.Listing =>
              // contiene tutti i riferimenti agli attori che si sono iscritti ad un certo servizio
              println(s"New Firestation! $msg")
              // dammi tutti i servizxi di che sono di AntsRender e mettili in una lista
              val services = msg.serviceInstances(FireStation.fireStationServiceKey).toList
              // se ho gia ricevuto questo frontend allora non cambia nulla ..
              if (services == fireStations)
                Behaviors.same
              else
              // se inevece è nuovo allora a tutti i sensori gli passo la nuova lista di frontend

              Behaviors.same
//            case FindFireStation() =>
//              // (4) send a Find message to the Receptionist, saying
//              // that we want to find any/all listings related to
//              // FireStation.fireStationServiceKey
//              println(s"Sensor: got a FindFireStation message")
//              ctx.system.receptionist !
//                Receptionist.Find(FireStation.fireStationServiceKey, listingAdapter)
//              Behaviors.same
//            case ListingResponse(FireStation.fireStationServiceKey.listing(listings)) =>
//              // (5) after Step 4, the Receptionist sends us this
//              // ListingResponse message. the `listings` variable is
//              // a Set of ActorRef of type Message, which
//              // you can interpret as “a set of FireStation ActorRefs.” for
//              // this example i know that there will be at most one
//              // FireStation actor, but in other cases there may be more
//              // than one actor in this set.
//              println(s"Sensor: got a ListingResponse message")
//              // i add this line just to be clear about `listings` type
//              val xs: Set[ActorRef[Message]] = listings
//              // loop through all of the ActorRefs
//              for (x <- xs) {
//                // there should be only one ActorRef, so i assign it
//                // to the `mouth` variable i created earlier
//                fireStation = Some(x)
//                // send a SpeakText message to the Mouth actor
//                fireStation.foreach{ m =>
//                  m ! sensorInAlarm()
//                }
//              }
//              Behaviors.same

        })
      })*/
    })

  def sensorLogic(position: (Int, Int),
                  ctx: ActorContext[SensorCommand|Receptionist.Listing],
                  fireStations: List[ActorRef[FireStationCommand]): Behavior[SensorCommand|Receptionist.Listing] =
    Behaviors.withTimers( timers => {
      Behaviors.receiveMessage(msg => {
        msg match
          case msg:Receptionist.Listing =>
            // contiene tutti i riferimenti agli attori che si sono iscritti ad un certo servizio
            println(s"New Firestation! $msg")
            // dammi tutti i servizxi di che sono di AntsRender e mettili in una lista
            val services = msg.serviceInstances(FireStation.fireStationServiceKey).toList
            // se ho gia ricevuto questo frontend allora non cambia nulla ..
            if (services == fireStations)
              Behaviors.same
            else
              // se inevece è nuovo allora a tutti i sensori gli passo la nuova lista di frontend
              sensorLogic(position, ctx, msg.serviceInstances(FireStation.fireStationServiceKey).toList)
              Behaviors.same

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


@main
def createSensors(): Unit =
  val cityGrid = CityGrid(300, 300)
  cityGrid.createCityGrid(3, 3)
  cityGrid.zones.foreach(z => SensorActor(new Sensor(Random.between(z.bounds.x0.toInt, z.bounds.x1.toInt),
    Random.between(z.bounds.y0.toInt, z.bounds.y1.toInt))))


