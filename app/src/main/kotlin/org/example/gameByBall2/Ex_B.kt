package org.example.gameByBall2

import processing.core.PApplet

object ExB {
    class Main : PApplet() {
        override fun settings() {
            size(1000, 100)
            noLoop()
        }

        override fun draw() {
            val array = IntArray(10)
            val rad = array.map { random(10f, 100f).toInt() }
            for ((i, r) in rad.withIndex()) {
                ellipse(i * 100f + 50f, 50f, r.toFloat(), r.toFloat())
            }
        }
    }

    fun main() {
        PApplet.main(Main::class.java.name)
    }
}
