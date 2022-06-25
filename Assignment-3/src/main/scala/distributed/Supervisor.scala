package distributed

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.*
import akka.cluster.typed.Cluster
import com.typesafe.config.ConfigFactory
import concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.Random

object Supervisor :
  def apply(): Behavior[Nothing] = Behaviors.setup { ctx =>
    val cluster = Cluster(ctx.system)

    if (cluster.selfMember.hasRole("GUI")) ???
    else if (cluster.selfMember.hasRole("SENSOR")) ???
    else ???
    Behaviors.empty
  }


@main def demo: Unit =
  ???