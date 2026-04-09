package org.example.gameByBall2

import processing.core.PApplet
import kotlin.random.Random
import kotlin.math.hypot

object ExC {
	data class Vector(val x: Float, val y: Float) {
		operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
		operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)
		operator fun times(scalar: Float) = Vector(x * scalar, y * scalar)
		operator fun div(scalar: Float) = Vector(x / scalar, y / scalar)
		operator fun unaryMinus() = Vector(-x, -y)
	}

	class Shape(
		val radius: Float,
		var position: Vector = Vector(0f, 0f),
		var speed: Vector = Vector(0f, 0f),
		val restitution: Float = 0.95f,
		val isMouseControlled: Boolean = false
	)

	class Field {
		val x = 1000
		val y = 1000

		/** 重力加速度 */
		val gravity = 0.3f

		/** 空気抵抗 */
		val airResistance = 0.99f

		/** 接線方向の壁摩擦 */
		val wallFriction = 0.95f

		/** 反発係数 */
		val restitution = 0.9f

		/** ボールの数 */
		val ballCount = 200

		/** マウスからの斥力の強さ */
		val mouseRepulsionForce = 1f

		/** マウスからの斥力の範囲 */
		val mouseRepulsionRange = 150f

		/** マウスの位置 */
		val mouse = Shape(50f, Vector(0f, 0f), Vector(0f, 0f), 0f, isMouseControlled = true)

		var shapes = MutableList(ballCount) {
			Shape(
				Random.nextDouble(10.toDouble(), 20.toDouble()).toFloat(),
				Vector(Random.nextDouble(0.toDouble(), x.toDouble()).toFloat(), 30f),
				Vector(Random.nextDouble((-10).toDouble(), 10.toDouble()).toFloat(), 0f),
			)
		}

		constructor() {
			shapes.add(mouse)
		}

		fun addShape(shape: Shape) {
			shapes.add(shape)
		}

		fun compute() {
			val newVelocities = shapes.map { it.speed }.toMutableList()
			val newPositions = shapes.map { it.position }.toMutableList()

			for ((i, shape) in shapes.withIndex()) {
				if (!shape.isMouseControlled) {
					// 重力加速度の加算
					var velocity = newVelocities[i] + Vector(0f, gravity)

					// 空気抵抗の計算
					velocity *= airResistance

					val deltaFromMouse = newPositions[i] - mouse.position
					val distFromMouse = hypot(deltaFromMouse.x, deltaFromMouse.y)

					// マウスから斥力を発生させる
					if (distFromMouse < mouseRepulsionRange && distFromMouse > 0f) {
						val repulsionStrength = mouseRepulsionForce * (1f - distFromMouse / mouseRepulsionRange)
						val repulsionDirection = deltaFromMouse / distFromMouse
						velocity += repulsionDirection * repulsionStrength
					}

					var newPosition = newPositions[i] + velocity

					// 左右壁判定
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

					// 上下壁判定
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
			// 物体同士の衝突判定
			for (i in shapes.indices) {
				for (j in (i + 1) until shapes.size) {
					val shape = shapes[i]
					val other = shapes[j]

					val delta = newPositions[j] - newPositions[i]
					val dist = hypot(delta.x, delta.y)

					val minDist = shape.radius + other.radius

					if (dist < minDist && dist > 0f) {
						/** 単位法線ベクトル */
						val normal = Vector(delta.x, delta.y) / dist

						// マウスによる衝突は物体をは一方的に押す
						if (shape.isMouseControlled) {
							newPositions[j] = newPositions[i] + normal * minDist
						} else if (other.isMouseControlled) {
							newPositions[i] = newPositions[j] - normal * minDist
						} else {
							/** 相対速度ベクトル */
							val relVel = newVelocities[i] - newVelocities[j]

							/** 接近速度 */
							val sepVel = relVel.x * normal.x + relVel.y * normal.y

							if (sepVel > 0) {
								val restitution = minOf(shape.restitution, other.restitution)
								val impulse = (1 + restitution) * sepVel / 2f

								newVelocities[i] -= normal * impulse
								newVelocities[j] += normal * impulse
							}
							// 重なり修正
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

		override fun settings() {
			field = Field()
			size(field.x, field.y)
		}

		override fun draw() {
			noCursor()
			background(color(255, 255, 255))
			for ((index, shape) in field.shapes.withIndex()) {
				if (index == field.shapes.size - 1) {
					stroke(color(255, 0, 0))
				} else {
					stroke(color(0, 0, 0))
				}
				fill(color(255, 255, 255))
				ellipse(shape.position.x, shape.position.y, shape.radius * 2, shape.radius * 2)
			}
			field.mouse.position = Vector(mouseX.toFloat(), mouseY.toFloat())
			field.compute()
		}
	}

	fun main() {
		PApplet.main(Main::class.java.name)
	}
}
