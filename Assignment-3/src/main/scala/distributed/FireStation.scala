package distributed

import distributed.Message
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector, SupervisorStrategy, Terminated}
import akka.actor.typed.scaladsl.Behaviors

class FireStation :
  // in caso di allarme la stazione aggiorna la GUI (la zona allarmata diventa gialla)
  // dopo X secondi l'attore firestation si automanda un messaggio di inizio assistenza
  case class Alarm() extends Message
  // quando arriva questo messaggio la zona della GUI diventa blu e tornerà alla normalità solo dopo che un bottone
  // sulla GUI viene premuto che indica che l'allarme è stato gestito
  // cambio lo status
  case class StartAssistance() extends Message
  // quando arriva questo messaggio vuol dire che ho premuto la fine dell'assistenza da GUI tramite un bottone e la caserma ritorna libera
  // gestire gli altri eventuali messaggi nello Stash
  // cambio lo status
  case class EndAssistance() extends Message
  // getInfo in cui ritorna lo stato della stazione
  case class GetInfo() extends Message

  enum Status:
    case Busy
    case Normal

  val status: Status = Status.Normal

  def apply(): Behavior[Message] = Behaviors.setup( ctx => {
    // operazioni preliminari per quando viene istanziato l'attore caserma
    // vanno fatte qui sotto

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


