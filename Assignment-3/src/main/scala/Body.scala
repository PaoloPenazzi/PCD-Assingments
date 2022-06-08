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

  private class Velocity2dImpl(override val x: Double, override val y: Double) extends Velocity2d:
    def fromPositions(from: Position2d, to: Position2d): Velocity2d = Velocity2d(to.x - from.x, to.y - from.y)
    def scalarMul(k: Double): Velocity2d = Velocity2d(x * k, y * k)
    def normalize: Velocity2d =
      val mod: Double = Math.sqrt(x * x + y * y)
      if(mod > 0) Velocity2d(x / mod, y / mod) else throw IllegalStateException()
    def sum(v: Velocity2d): Velocity2d = Velocity2d(x + v.x, y + v.y)


case class Position2d(x: Double, y: Double):
  def sum(v: Velocity2d): Position2d = Position2d(x + v.x, y + v.y)


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

@main
def test(): Unit = {
  println(Position2d(3, 2))
}

