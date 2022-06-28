package distributed

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.*
import akka.cluster.typed.Cluster
import com.typesafe.config.ConfigFactory

import concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.Random

object Supervisor :

  def apply(zone: String,
            position: (Int, Int)): Behavior[Nothing] = Behaviors.setup { ctx =>
    val cluster = Cluster(ctx.system)
    if cluster.selfMember.hasRole("SENSOR")
    then
      ctx.spawnAnonymous(SensorActor(position, zone))
    else
      ctx.spawnAnonymous(FireStationActor(position, zone))
    Behaviors.empty
  }

  def apply(city: CityGrid): Behavior[Nothing] = Behaviors.setup { ctx =>
    val viewActor = ctx.spawn(ViewActor(), "View")
    viewActor ! StartGUI(city)
    Behaviors.empty
  }

  def randomPosition(zone: Zone): (Int, Int) = {
    (Random.between(zone.bounds.x0.toInt + 10, zone.bounds.x1.toInt - 10),
      Random.between(zone.bounds.y0.toInt + 10, zone.bounds.y1.toInt - 10))
  }


@main def demo(): Unit =
  val cityGrid = CityGrid(200, 200)
  cityGrid.createCityGrid(2, 2)
  var port = 2551
  startupWithRole("GUI", 7593)(Supervisor(cityGrid))
  for
    z <- cityGrid.zones
  do
    startupWithRole("STATION", port)(Supervisor(z.id, Supervisor.randomPosition(z)))
    port = port + 1
  for
    z <- cityGrid.zones
  do
    startupWithRole("SENSOR", port)(Supervisor(z.id, Supervisor.randomPosition(z)))
    port = port + 1
  
