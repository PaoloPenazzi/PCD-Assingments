package distributed

import akka.actor.typed.ActorRef
import it.unibo.pcd.assignment.Message

import java.awt.event.{ActionEvent, WindowAdapter, WindowEvent}
import java.awt.{BorderLayout, Color, Graphics, Graphics2D, RenderingHints}
import javax.swing.{JButton, JFrame, JPanel}
import scala.collection.mutable.ListBuffer

trait View:
  def display(city: CityGrid): Unit
  def start(zones: List[Zone]): Unit

object View:
  def apply(width: Int, height: Int, viewActor: ActorRef[ViewCommand]): View = new ViewFrame(width, height, viewActor)

  private class ViewFrame(width: Int, height: Int, viewActor: ActorRef[ViewCommand]) extends JFrame, View :
    val cityPanel = new CityPanel(width, height)
    val controlPanel = new ControlPanel(viewActor)

    override def start(zones: List[Zone]): Unit =
      setSize(width, height)
      setLayout(new BorderLayout())
      setTitle("Smart City Simulation")
      setLocationRelativeTo(null)
      controlPanel.setupView(zones)
      cityPanel.setup()
      getContentPane.add(cityPanel)
      getContentPane.add(controlPanel, BorderLayout.NORTH)
      addWindowListener(new WindowAdapter() {
        override def windowClosing(ev: WindowEvent): Unit = System.exit(-1)
        override def windowClosed(ev: WindowEvent): Unit = System.exit(-1)
      })
      setVisible(true)

    override def display(city: CityGrid): Unit =
      cityPanel.city = Option(city)
      repaint()

class CityPanel(width: Int, height: Int) extends JPanel:
  var city: Option[CityGrid] = None

  override def paint(g: Graphics): Unit =
    if city.isDefined
    then
      val g2: Graphics2D = g.asInstanceOf
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
      g2.clearRect(0, 0, 2 * width, 2 * height)
      g2.drawRect(50, 50, city.get.bounds.x1.toInt, city.get.bounds.y1.toInt)
      for x <- city.get.zones
        do
          g2.setColor(Color.BLACK)
          g2.drawRect(x.bounds.x0.toInt + 50,
            x.bounds.y0.toInt + 50,
            x.bounds.x1.toInt - x.bounds.x0.toInt,
            x.bounds.y1.toInt - x.bounds.y0.toInt)
          if city.get.zonesAlarmed.contains(x)
          then
            g2.setColor(Color.YELLOW)
            g2.fillRect(x.bounds.x0.toInt + 51,
              x.bounds.y0.toInt + 51,
              x.bounds.x1.toInt - x.bounds.x0.toInt - 1,
              x.bounds.y1.toInt - x.bounds.y0.toInt - 1)
      for x <- city.get.sensors
        do
          if city.get.sensors(x._1) then g2.setColor(Color.RED) else g2.setColor(Color.BLUE)
          if city.get.sensorsDisconnected.contains(x._1) then g2.setColor(Color.WHITE)
          g2.drawOval(x._1._1 + 50, x._1._2 + 50, 8, 8)
      for x <- city.get.fireStations
        do
          if city.get.fireStations(x._1) then g2.setColor(Color.GRAY) else g2.setColor(Color.GREEN)
          g2.drawRect(x._1._1 + 50, x._1._2 + 50, 8, 8)

  def setup(): Unit =
    setSize(width + 100, height + 100)
    setVisible(true)

class ControlPanel(actor: ActorRef[ViewCommand]) extends JPanel:
  var buttons: ListBuffer[JButton] = ListBuffer.empty

  def setupView(zones: List[Zone]): Unit =
    zones.foreach(z => {
      val button = new JButton(z.id)
      buttons += button
      add(button)
      button.addActionListener(actionPerformed(_))
    })
    setVisible(true)

  def actionPerformed(event: ActionEvent): Unit =
    val id: String = event.getSource.asInstanceOf[JButton].getText
    actor ! ResetAlarm(id)