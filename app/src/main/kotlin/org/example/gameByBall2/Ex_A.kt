package org.example.gameByBall2

import processing.core.PApplet

object ExA {
	class First : PApplet() {
		private val count = 10

		override fun settings() {
			size((count + 1) * 100, (count + 1) * 100)
		}

		override fun draw() {
			for (i in 0 until count) {
				if (i % 3 == 0) {
					fill(color(0, 0, 255))
				} else {
					fill(color(255, 255, 255))
				}
				ellipse(i * 100f + 100f, i * 100f + 100f, 100f, 100f)
			}
		}
	}

	class Third : PApplet() {
		private val fieldWidth = 5
		private val fieldHeight = 5

		override fun settings() {
			size((fieldWidth + 1) * 100, (fieldHeight + 1) * 100)
		}

		override fun draw() {
			var count = 0
			for (x in 0 until fieldWidth) {
				for (y in 0 until fieldHeight) {
					when {
						count % 7 == 0 && count % 3 == 0 -> fill(color(0, 255, 255))
						count % 7 == 0 -> fill(color(0, 0, 255))
						count % 3 == 0 -> fill(color(0, 255, 0))
						else -> fill(color(255, 255, 255))
					}
					ellipse(x * 100f + 100f, y * 100f + 100f, 80f, 80f)
					count++
				}
			}
		}
	}

	fun main() {
		PApplet.main(First::class.java.name)
		PApplet.main(Third::class.java.name)
	}
}
