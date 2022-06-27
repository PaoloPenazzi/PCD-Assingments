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

    if (cluster.selfMember.hasRole("GUI")) ???
    else if (cluster.selfMember.hasRole("SENSOR")) ctx.spawn(SensorActor(position, id, zone))
    else ctx.spawn(FireStationActor(position, id, zone))
    Behaviors.empty
  }


@main def demo: Unit =
  val cityGrid = CityGrid(300, 300)
  cityGrid.createCityGrid(3, 3)
  for
    x <- 8081 to (8081 + cityGrid.zones.size)
    z <- cityGrid.zones
  yield
    startupWithRole("SENSOR", x)(Supervisor("Sensor"+ z.id, z.id,
      (Random.between(z.bounds.x0.toInt, z.bounds.x1.toInt), Random.between(z.bounds.y0.toInt, z.bounds.y1.toInt))))
  for
    x <- 8100 to (8100 + cityGrid.zones.size)
    z <- cityGrid.zones
  yield
    startupWithRole("STATION", x)(Supervisor("Station"+ z.id, z.id,
      (Random.between(z.bounds.x0.toInt, z.bounds.x1.toInt), Random.between(z.bounds.y0.toInt, z.bounds.y1.toInt))))
      