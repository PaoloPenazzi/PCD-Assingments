import akka.actor.typed.ActorRef

import scala.collection.mutable
import scala.util.Random

enum Command:
  case StartSimulation
  case StartGUI
  case ResumeSimulation
  case StopSimulation
  case VelocityDoneResponse(result: Body)
  case PositionDoneResponse(result: Body)
  case ComputeVelocityRequest(bodies: mutable.Seq[Body], replyTo: ActorRef[Command.VelocityDoneResponse])
  case ComputePositionRequest(boundary: Boundary, replyTo: ActorRef[Command.PositionDoneResponse])
  case UpdateGUI(bodies: mutable.Seq[Body], virtualTime: Double, iteration: Int, bounds: Boundary)

case class Simulation(numBodies: Int,
                      var iteration: Int,
                      sideLength: Int):
  val boundary: Boundary =
    Boundary(-sideLength, -sideLength, sideLength, sideLength)
  var bodies: mutable.Seq[Body] =
    var myBodies: mutable.Seq[Body] = mutable.Seq.empty
    for (id <- 0 until numBodies)
      val x = boundary.x0 * 0.25 + Random.nextDouble() * (boundary.x1 - boundary.x0) * 0.25
      val y = boundary.y0 * 0.25 + Random.nextDouble() * (boundary.y1 - boundary.y0) * 0.25
      val body = Body(id, Position2d(x, y), Velocity2d(0, 0), 10)
      myBodies = myBodies :+ body
    myBodies

trait Velocity2d:
  def x: Double
  def y: Double
  def fromPositions(from: Position2d, to: Position2d): Velocity2d
  def scalarMul(k: Double): Velocity2d
  def normalize: Velocity2d
  def sum(v: Velocity2d): Velocity2d

object Velocity2d:
  def apply(x: Double, y: Double): Velocity2d = Velocity2dImpl(x, y)
  def apply(from: Position2d, to: Position2d): Velocity2d = Velocity2dImpl(to.x - from.x, to.y - from.y)
  def apply(velocity: Velocity2d): Velocity2d = Velocity2dImpl(velocity.x, velocity.y)

  private case class Velocity2dImpl(override val x: Double, override val y: Double) extends Velocity2d:
    def fromPositions(from: Position2d, to: Position2d): Velocity2d = Velocity2d(to.x - from.x, to.y - from.y)
    def scalarMul(k: Double): Velocity2d = Velocity2d(x * k, y * k)
    def normalize: Velocity2d =
      val mod: Double = Math.sqrt(x * x + y * y)
      if(mod > 0) Velocity2d(x / mod, y / mod) else throw IllegalStateException()
    def sum(v: Velocity2d): Velocity2d = Velocity2d(x + v.x, y + v.y)


case class Position2d(x: Double, y: Double):
  def sum(v: Velocity2d): Position2d = Position2d(x + v.x, y + v.y)


case class Boundary(x0: Double, y0: Double, x1: Double, y1: Double)

case class Body(id: Int, var position: Position2d, var velocity: Velocity2d, mass: Double):
  val repulsiveConst: Double = 0.01
  val frictionConst: Double = 1

  def equals(obj: Body): Boolean = id == obj.id

  def updatePosition(deltaTime: Double): Unit =
    position = position.sum(velocity.scalarMul(deltaTime))

  def updateVelocity(acceleration: Velocity2d, deltaTime: Double): Unit =
    velocity = velocity.sum(acceleration.scalarMul(deltaTime))

  def getDistanceFrom(body: Body): Double =
    val deltaX: Double = position.x - body.position.x
    val deltaY: Double = position.y - body.position.y
    Math.sqrt(deltaX * deltaX + deltaY * deltaY)

  def computeRepulsiveForceBy(body: Body): Velocity2d =
    val distance: Double = getDistanceFrom(body)
    Velocity2d(body.position, position).normalize.scalarMul(body.mass * repulsiveConst / (distance * distance))
    
  def getCurrentFrictionForce: Velocity2d =
    Velocity2d(velocity).scalarMul(-frictionConst)
    
  def checkAndSolveBoundaryCollision(bounds: Boundary): Unit =
    val x: Double = position.x
    val y: Double = position.y

    x match
      case x if x > bounds.x1 => 
        position = Position2d(bounds.x1, position.y)
        velocity = Velocity2d(-velocity.x, velocity.y)
      case x if x < bounds.x0 =>
        position = Position2d(bounds.x0, position.y)
        velocity = Velocity2d(-velocity.x, velocity.y)
      case _ =>

    y match
      case y if y > bounds.y1 =>
        position = Position2d(position.x, bounds.y1)
        velocity = Velocity2d(velocity.x, -velocity.y)
      case y if y < bounds.y0 =>
        position = Position2d(position.x, bounds.y0)
        velocity = Velocity2d(velocity.x, -velocity.y)
      case _ =>

  def computeBodyVelocity(bodies: mutable.Seq[Body]): Unit =
    var totalForce: Velocity2d = Velocity2d(0,0)
    bodies.filter(!_.equals(this)).foreach(b => totalForce = totalForce.sum(this.computeRepulsiveForceBy(b)))
    totalForce = totalForce.sum(this.getCurrentFrictionForce)
    val acceleration: Velocity2d = Velocity2d(totalForce).scalarMul(1.0 / this.mass)
    this.updateVelocity(acceleration, 0.001)

  def computeBodyPosition(boundary: Boundary): Unit =
    this.updatePosition(0.001)
    this.checkAndSolveBoundaryCollision(boundary)