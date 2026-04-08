package org.example.gameByBall2

import processing.core.PApplet

object ExC {
	data class Coordinate(val x: Float, val y: Float) {
		operator fun plus(other: Coordinate) = Coordinate(x + other.x, y + other.y)
		operator fun minus(other: Coordinate) = Coordinate(x - other.x, y - other.y)
		operator fun times(scalar: Float) = Coordinate(x * scalar, y * scalar)
		operator fun unaryMinus() = Coordinate(-x, -y)
	}

	class Shape(
		val radius: Float,
		var position: Coordinate = Coordinate(0f, 0f),
		var speed: Coordinate = Coordinate(0f, 0f)
	)

	class Field {
		val x = 500
		val y = 500

		/* 重力加速度*/
		val gravity = 0.3f

		/* 空気抵抗 */
		val airResistance = 0.99f

		/*接線方向の壁摩擦*/
		val wallFriction = 0.95f

		/* 反発係数 */
		val restitution = 0.95f
		var shapes = mutableListOf(Shape(10f, Coordinate(50f, 50f), Coordinate(5f, 2f)))

		fun addShape(shape: Shape) {
			shapes.add(shape)
		}

		fun compute() {
			for (shape in shapes) {
				// 重力加速度の加算
				var velocity = shape.speed + Coordinate(0f, gravity)

				// 空気抵抗の計算
				velocity *= airResistance

				var newPosition = shape.position + velocity

				// 左右壁判定
				if (newPosition.x - shape.radius < 0) {
					newPosition = Coordinate(shape.radius, newPosition.y)
					velocity = Coordinate(-velocity.x * restitution, velocity.y * wallFriction)
				} else if (newPosition.x + shape.radius > x) {
					newPosition = Coordinate(x - shape.radius, newPosition.y)
					velocity = Coordinate(-velocity.x * restitution, velocity.y * wallFriction)
				}

				// 上下壁判定
				if (newPosition.y - shape.radius < 0) {
					newPosition = Coordinate(newPosition.x, shape.radius)
					velocity = Coordinate(velocity.x * wallFriction, -velocity.y * restitution)
				} else if (newPosition.y + shape.radius > y) {
					newPosition = Coordinate(newPosition.x, y - shape.radius)
					velocity = Coordinate(velocity.x * wallFriction, -velocity.y * restitution)
				}

				shape.speed = velocity
				shape.position = newPosition
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
			for (shape in field.shapes) {
				ellipse(shape.position.x, shape.position.y, shape.radius * 2, shape.radius * 2)
			}
			field.compute()
		}
	}

	fun main() {
		PApplet.main(Main::class.java.name)
	}
}
