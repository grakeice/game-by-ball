package org.example.gameByBall2

import processing.core.PApplet
import kotlin.random.Random
import kotlin.math.hypot

object ExC {
	data class Coordinate(val x: Float, val y: Float) {
		operator fun plus(other: Coordinate) = Coordinate(x + other.x, y + other.y)
		operator fun minus(other: Coordinate) = Coordinate(x - other.x, y - other.y)
		operator fun times(scalar: Float) = Coordinate(x * scalar, y * scalar)
		operator fun div(scalar: Float) = Coordinate(x / scalar, y / scalar)
		operator fun unaryMinus() = Coordinate(-x, -y)
	}

	class Shape(
		val radius: Float,
		var position: Coordinate = Coordinate(0f, 0f),
		var speed: Coordinate = Coordinate(0f, 0f),
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

		/** マウスの位置 */
		val mouse = Shape(50f, Coordinate(0f, 0f), Coordinate(0f, 0f), 0f, isMouseControlled = true)

		var shapes = MutableList(ballCount) {
			Shape(
				Random.nextDouble(10.toDouble(), 20.toDouble()).toFloat(),
				Coordinate(Random.nextDouble(0.toDouble(), x.toDouble()).toFloat(), 30f),
				Coordinate(Random.nextDouble((-10).toDouble(), 10.toDouble()).toFloat(), 0f),
			)
		}

		constructor() {
			shapes.add(mouse)
		}

		fun addShape(shape: Shape) {
			shapes.add(shape)
		}

		fun compute() {
			// 新しい速度と位置を一時保存
			val newVelocities = shapes.map { it.speed }.toMutableList()
			val newPositions = shapes.map { it.position }.toMutableList()

			for ((i, shape) in shapes.withIndex()) {

				// 重力加速度の加算
				var velocity = newVelocities[i] + Coordinate(0f, gravity)

				// 空気抵抗の計算
				velocity *= airResistance

				var newPosition = newPositions[i] + velocity

				// 左右壁判定
				if (newPosition.x - shape.radius < 0) {
					newPosition = Coordinate(shape.radius, newPosition.y)
					velocity = Coordinate(
						-velocity.x * restitution * shape.restitution,
						velocity.y * wallFriction * shape.restitution
					)
				} else if (newPosition.x + shape.radius > x) {
					newPosition = Coordinate(x - shape.radius, newPosition.y)
					velocity = Coordinate(
						-velocity.x * restitution * shape.restitution,
						velocity.y * wallFriction * shape.restitution
					)
				}

				// 上下壁判定
				if (newPosition.y - shape.radius < 0) {
					newPosition = Coordinate(newPosition.x, shape.radius)
					velocity = Coordinate(
						velocity.x * wallFriction * shape.restitution,
						-velocity.y * restitution * shape.restitution
					)
				} else if (newPosition.y + shape.radius > y) {
					newPosition = Coordinate(newPosition.x, y - shape.radius)
					velocity = Coordinate(
						velocity.x * wallFriction * shape.restitution,
						-velocity.y * restitution * shape.restitution
					)
				}

				newVelocities[i] = velocity
				newPositions[i] = newPosition
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
						val normal = Coordinate(delta.x, delta.y) / dist

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
			field.mouse.position = Coordinate(mouseX.toFloat(), mouseY.toFloat())
			field.compute()
		}
	}

	fun main() {
		PApplet.main(Main::class.java.name)
	}
}
