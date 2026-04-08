package org.example.gameByBall2

import processing.core.PApplet

object ExC {
    data class Coordinate(val x: Float, val y: Float)

    class Shape(
        val radius: Float,
        var position: Coordinate = Coordinate(0f, 0f),
        var speed: Coordinate = Coordinate(0f, 0f)
    )

    class Field {
        val x = 500
        val y = 500
        var shapes = mutableListOf(Shape(50f, Coordinate(50f, 50f), Coordinate(5f, 2f)))

        fun addShape(shape: Shape) {
            shapes.add(shape)
        }

        fun compute() {
            for (shape in shapes) {
                var vx = shape.speed.x
                var vy = shape.speed.y
                var nx = shape.position.x + vx
                var ny = shape.position.y + vy

                if (nx - shape.radius < 0 || nx + shape.radius > x) {
                    vx = -vx
                    nx = shape.position.x + vx
                }

                if (ny - shape.radius < 0 || ny + shape.radius > y) {
                    vy = -vy
                    ny = shape.position.y + vy
                }

                shape.speed = Coordinate(vx, vy)
                shape.position = Coordinate(nx, ny)
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
                ellipse(shape.position.x, shape.position.y, shape.radius, shape.radius)
            }
            field.compute()
        }
    }

    fun main() {
        PApplet.main(Main::class.java.name)
    }
}
