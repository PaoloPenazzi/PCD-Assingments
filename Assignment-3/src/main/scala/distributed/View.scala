package distributed

import java.awt.{BorderLayout, Graphics, Graphics2D, RenderingHints}
import javax.swing.{JFrame, JPanel}

trait View:
  def display(city: CityGrid): Unit
  def start(): Unit

object View:
  def apply(width: Int, height: Int): View = new ViewFrame(width, height)

  private class ViewFrame(width: Int, height: Int) extends JFrame, View :
    val cityPanel = new CityPanel(width, height)

    override def start(): Unit =
      setSize(width, height)
      setLayout(new BorderLayout())
      setTitle("Smart City Simulation")
      setResizable(true)

    override def display(city: CityGrid): Unit =
      cityPanel.city = Option(city)
      repaint()

class CityPanel(width: Int, height: Int) extends JPanel:
  var iteration: Int = 0
  var city: Option[CityGrid] = None
  val dx: Int = width / 2 - 20
  val dy: Int = height / 2 - 20
  var scale: Double = 1

  private def getYCoordinate(y: Double): Int = (dy - y * dy * scale).toInt
  private def getXCoordinate(x: Double): Int = (dx + x * dx * scale).toInt
  override def paint(g: Graphics): Unit =
    if city.isDefined
    then
      val g2: Graphics2D = g.asInstanceOf
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
      g2.clearRect(0, height, width, height)
      val x0 = getXCoordinate(city.get.bounds.x0)
      val y0 = getYCoordinate(city.get.bounds.y0)
      val wd = getXCoordinate(city.get.bounds.x1) - x0
      val ht = y0 - getYCoordinate(city.get.bounds.y1)
      g2.drawRect(x0, y0 - ht, wd, ht)

  def setup(): Unit =
    setSize(width, height)

