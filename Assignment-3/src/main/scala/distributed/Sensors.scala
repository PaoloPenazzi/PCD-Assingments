package distributed

import it.unibo.pcd.assignment.Message

import scala.util.Random

class Sensor(id: Int, var x: Int, var y:Int):
  var level: Option[Double] = None

  def generateValueOrFailure(): Unit =
    val random = Random.between(0.0, 7.0)
    if random > 5 then level = None else level = Option(random)

object SensorActor:
  def apply(sensor: Sensor): Behavior[Message] =
    Behavior
    


@main
def createSensors(): Unit =
  val cityGrid = CityGrid(300, 300)
  cityGrid.createCityGrid(3, 3)
  cityGrid.zones.foreach(z => new Sensor(0,
    Random.between(z.bounds.x0.toInt, z.bounds.x1.toInt),
    Random.between(z.bounds.y0.toInt, z.bounds.y1.toInt)))

