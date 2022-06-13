package distributed

import it.unibo.pcd.assignment.Boundary

import scala.util.Random

class Sensor(id: Int):
  var level: Option[Double] = None

  def generateValueOrFailure(): Unit =
    val random = Random.between(0.0, 7.0)
    if random > 5 then level = None else level = Option(random)


class Station(id: String):
  var alarm = false

trait Zone:
  def bounds: Boundary
  def id: String
  def station: Station
  def sensors: List[Sensor]

object Zone:
  def apply(id: String, fromX: Int, toX: Int, fromY: Int, toY: Int): Zone = new ZoneImpl(id, fromX, toX, fromY, toY)

  private class ZoneImpl(override val id: String, fromX: Int, toX: Int, fromY: Int, toY: Int) extends Zone:
    override def bounds: Boundary = Boundary(fromX, fromY, toX, toY)
    override def sensors: List[Sensor] = List.empty
    override def station: Station = new Station(id)

class CityGrid(width: Int, height: Int):
  var zones: List[Zone] = List.empty
  var bounds: Boundary = Boundary(0, width, 0, height)

  def createZones(rows: Int, cols: Int): Unit =
    val sizeX: Double = width/cols
    val sizeY: Double = height/rows
    for
      x <- 0 until rows
      y <- 0 until cols
    do
      val newZone = Zone((y + x.toString), (sizeX * y).toInt, (sizeX * (y + 1)).toInt, (sizeY * x).toInt, (sizeY * (x + 1)).toInt)
      zones = zones :+ newZone

@main
def testGrid(): Unit =
  val city = new CityGrid(200, 200)
  city.createZones(4, 4)
  city.zones.foreach(z => println(z.id + "  " + z.bounds))
