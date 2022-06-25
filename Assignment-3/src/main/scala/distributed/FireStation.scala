package distributed

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import distributed.Message
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors
import distributed.Message

sealed trait FireStationCommand extends Message
// in caso di allarme la stazione aggiorna la GUI (la zona allarmata diventa gialla)
// dopo X secondi l'attore firestation si automanda un messaggio di inizio assistenza
case class Alarm() extends FireStationCommand
// quando arriva questo messaggio la zona della GUI diventa blu e tornerà alla normalità solo dopo che un bottone
// sulla GUI viene premuto che indica che l'allarme è stato gestito
// cambio lo status
case class StartAssistance() extends FireStationCommand
// quando arriva questo messaggio vuol dire che ho premuto la fine dell'assistenza da GUI tramite un bottone e la caserma ritorna libera
// gestire gli altri eventuali messaggi nello Stash
// cambio lo status
case class EndAssistance() extends FireStationCommand
// getInfo in cui ritorna lo stato della stazione
case class GetInfo() extends FireStationCommand
// messaggio che gli arriva dai sensori nel caso di allarme
case class sensorInAlarm() extends FireStationCommand

object FireStation :

  enum Status:
    case Busy
    case Normal

  // (1) a ServiceKey is a unique identifier for this actor
  val fireStationServiceKey = ServiceKey[Message]("fireStationTODO")

  val status: Status = Status.Normal

  def apply(): Behavior[Message] = Behaviors.setup( ctx => {
    // operazioni preliminari per quando viene istanziato l'attore caserma
    // vanno fatte qui sotto
    // la firestation si deve registrare per una certa zona al receptionist
    // (2) every actor that wants to be discoverable must register itself
    // with the Receptionist by sending the Receptionist as Receptionist
    // message, including your ServiceKey
    ctx.system.receptionist ! Receptionist.Register(fireStationServiceKey, ctx.self)

    Behaviors.receiveMessage( msg => {
      msg match
        case GetInfo() => ???
        case Alarm() => ???
        case StartAssistance() => ???
        case other => ???
    })
  })

  // MACRO BEHA: BUSY
  // La stazione qui è occupata e sta gestendo una assistenza
  def busyBehavior: Behavior[Message] = Behaviors.receive((ctx, msg) => {
    msg match
      case EndAssistance() => ???
      case other => ???
  })


