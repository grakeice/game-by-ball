package org.example.gameByBall

import processing.core.PApplet
import kotlin.math.hypot
import kotlin.random.Random
import ddf.minim.* // Minimをインポート

data class Vector(val x: Float, val y: Float) {
	operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
	operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)
	operator fun times(scalar: Float) = Vector(x * scalar, y * scalar)
	operator fun div(scalar: Float) = Vector(x / scalar, y / scalar)
	operator fun unaryMinus() = Vector(-x, -y)
}

class Shape(
	radius: Float,
	var position: Vector = Vector(0f, 0f),
	var speed: Vector = Vector(0f, 0f),
	var scale: Float = 1f,
	val restitution: Float = 0.95f,
	val isMouseControlled: Boolean = false,
) {
	val radius: Float
		get() = _radius * scale
	private val _radius: Float = radius
}

class Field {
	val x = 1000
	val y = 1000
	var gravity = 0.3f
	val airResistance = 0.99f
	val wallFriction = 0.95f
	val restitution = 0.9f
	val ballCount = 10
	var onCollision: ((impulse: Float) -> Unit)? = null
	val mouseRepulsionForce = 1f
	val mouseRepulsionRange = 150f
	val mouse = Shape(50f, Vector(0f, 0f), Vector(0f, 0f), 0f, isMouseControlled = true)

	var shapes = MutableList(ballCount) {
		Shape(
			Random.nextDouble(10.0, 20.0).toFloat(),
			Vector(Random.nextDouble(0.0, x.toDouble()).toFloat(), 30f),
			Vector(Random.nextDouble(-10.0, 10.0).toFloat(), 0f),
		)
	}

	init {
		shapes.add(mouse)
	}

	fun compute() {
		val newVelocities = shapes.map { it.speed }.toMutableList()
		val newPositions = shapes.map { it.position }.toMutableList()

		for ((i, shape) in shapes.withIndex()) {
			if (!shape.isMouseControlled) {
				var velocity = newVelocities[i] + Vector(0f, gravity)
				velocity *= airResistance

				val deltaFromMouse = newPositions[i] - mouse.position
				val distFromMouse = hypot(deltaFromMouse.x, deltaFromMouse.y)

				if (distFromMouse < mouseRepulsionRange && distFromMouse > 0f) {
					val repulsionStrength = mouseRepulsionForce * (1f - distFromMouse / mouseRepulsionRange)
					val repulsionDirection = deltaFromMouse / distFromMouse
					velocity += repulsionDirection * repulsionStrength
				}

				var newPosition = newPositions[i] + velocity

				// 壁判定
				if (newPosition.x - shape.radius < 0) {
					newPosition = Vector(shape.radius, newPosition.y)
					velocity = Vector(
						-velocity.x * restitution * shape.restitution,
						velocity.y * wallFriction * shape.restitution
					)
				} else if (newPosition.x + shape.radius > x) {
					newPosition = Vector(x - shape.radius, newPosition.y)
					velocity = Vector(
						-velocity.x * restitution * shape.restitution,
						velocity.y * wallFriction * shape.restitution
					)
				}

				if (newPosition.y - shape.radius < 0) {
					newPosition = Vector(newPosition.x, shape.radius)
					velocity = Vector(
						velocity.x * wallFriction * shape.restitution,
						-velocity.y * restitution * shape.restitution
					)
				} else if (newPosition.y + shape.radius > y) {
					newPosition = Vector(newPosition.x, y - shape.radius)
					velocity = Vector(
						velocity.x * wallFriction * shape.restitution,
						-velocity.y * restitution * shape.restitution
					)
				}

				newVelocities[i] = velocity
				newPositions[i] = newPosition
			}
		}

		for (i in shapes.indices) {
			for (j in (i + 1) until shapes.size) {
				val shape = shapes[i]
				val other = shapes[j]
				val delta = newPositions[j] - newPositions[i]
				val dist = hypot(delta.x, delta.y)
				val minDist = shape.radius + other.radius

				if (dist < minDist && dist > 0f) {
					val normal = Vector(delta.x, delta.y) / dist
					if (shape.isMouseControlled) {
						newPositions[j] = newPositions[i] + normal * minDist
					} else if (other.isMouseControlled) {
						newPositions[i] = newPositions[j] - normal * minDist
					} else {
						val relVel = newVelocities[i] - newVelocities[j]
						val sepVel = relVel.x * normal.x + relVel.y * normal.y
						if (sepVel > 0) {
							val res = minOf(shape.restitution, other.restitution)
							val impulse = (1 + res) * sepVel / 2f
							newVelocities[i] -= normal * impulse
							newVelocities[j] += normal * impulse
							onCollision?.invoke(impulse) // impulseをそのまま渡す
						}
						val overlap = minDist - dist
						newPositions[i] -= normal * (overlap / 2f)
						newPositions[j] += normal * (overlap / 2f)
					}
				}
			}
		}

		for ((i, shape) in shapes.withIndex()) {
			shape.speed = newVelocities[i]
			shape.position = newPositions[i]
		}
	}
}

class Main : PApplet() {
	lateinit var field: Field

	// Minim関連
	lateinit var minim: Minim
	lateinit var audioInput: AudioInput
	lateinit var hitSound: AudioSample

	var currentScale = 1f

	override fun settings() {
		field = Field()
		size(field.x, field.y)
	}

	override fun setup() {
		minim = Minim(this)

		audioInput = minim.getLineIn(Minim.MONO, 512)

		val resource = "audio/button.mp3"
		hitSound = minim.loadSample(resource, 512)

		field.onCollision = { impulse ->
			val gain = map(impulse, 0f, 20f, -20f, 6f).coerceIn(-40f, 6f)
			hitSound.gain = gain
			hitSound.trigger()
		}
	}

	override fun draw() {

		noCursor()
		background(255)

		val level = audioInput.mix.level()
		currentScale = 1 + map(level, 0f, 0.5f, 0f, 35f)

		for (shape in field.shapes) {
			if (shape.isMouseControlled) {
				shape.scale = currentScale
				stroke(255f, 0f, 0f)
			} else {
				shape.scale = 1f
				stroke(0)
			}
			fill(255)
			ellipse(shape.position.x, shape.position.y, shape.radius * 2, shape.radius * 2)
		}

		field.mouse.position = Vector(mouseX.toFloat(), mouseY.toFloat())
		field.compute()

	}

	override fun keyPressed() {
		when (key) {
			'+' -> field.gravity += 0.05f
			'-' -> field.gravity -= 0.05f
		}
	}

	override fun stop() {
		hitSound.close()
		audioInput.close()
		minim.stop()
		super.stop()
	}
}

fun main() {
	PApplet.main(Main::class.java.name)
}
