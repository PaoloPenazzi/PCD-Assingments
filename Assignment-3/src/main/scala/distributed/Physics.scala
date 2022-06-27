package distributed

import scala.util.Random
import it.unibo.pcd.assignment.Boundary

trait Zone:
  def bounds: Boundary
  def id: String

object Zone:
  def apply(id: String, fromX: Int, toX: Int, fromY: Int, toY: Int): Zone = new ZoneImpl(id, fromX, toX, fromY, toY)

  private class ZoneImpl(override val id: String, fromX: Int, toX: Int, fromY: Int, toY: Int) extends Zone:
    override def bounds: Boundary = Boundary(fromX, fromY, toX, toY)

class CityGrid(width: Int, height: Int):
  var zones: List[Zone] = List.empty
  var bounds: Boundary = Boundary(0, 0, width, height)
  var sensors: List[(Int,Int)] = List.empty
  var fireStations: List[(Int,Int)] = List.empty
  
  def createCityGrid(rows: Int, cols: Int): Unit =
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
  val city = new CityGrid(300, 300)
  city.createCityGrid(3, 3)
  val view: View = View(600, 650)
  view.start()
  view.display(city)
