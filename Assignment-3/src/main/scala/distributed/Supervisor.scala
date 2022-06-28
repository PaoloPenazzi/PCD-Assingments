package distributed

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.*
import akka.cluster.typed.Cluster
import com.typesafe.config.ConfigFactory

import concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.Random

object Supervisor :

  def apply(id: String,
            zone: String,
            position: (Int, Int)): Behavior[Nothing] = Behaviors.setup { ctx =>
    val cluster = Cluster(ctx.system)
    if cluster.selfMember.hasRole("SENSOR")
    then
      println("I am a sensor")
      ctx.spawn(SensorActor(position, id, zone), "Sensor" + id)
    else
      println("I am a station")
      ctx.spawn(FireStationActor(position, id), "Station" + id)
    Behaviors.empty
  }

  def apply(city: CityGrid): Behavior[Nothing] = Behaviors.setup { ctx =>
    println("I am a GUI")
    val viewActor = ctx.spawn(ViewActor(), "View")
    viewActor ! StartGUI(city)
    Behaviors.empty
  }


@main def demo(): Unit =
  val cityGrid = CityGrid(300, 300)
  cityGrid.createCityGrid(3, 3)
  var port = 2551
  for
    z <- cityGrid.zones
  do
    startupWithRole("STATION", port)(Supervisor("Station"+ z.id, z.id,
      (Random.between(z.bounds.x0.toInt, z.bounds.x1.toInt), Random.between(z.bounds.y0.toInt, z.bounds.y1.toInt))))
    port = port + 1
  for
    z <- cityGrid.zones
  do
    startupWithRole("SENSOR", port)(Supervisor("Sensor"+ z.id, z.id,
      (Random.between(z.bounds.x0.toInt, z.bounds.x1.toInt), Random.between(z.bounds.y0.toInt, z.bounds.y1.toInt))))
    port = port + 1
  startupWithRole("GUI", port)(Supervisor(cityGrid))
