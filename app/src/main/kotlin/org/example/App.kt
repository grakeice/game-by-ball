package org.example

import java.util.*

// Data class for Example definition
data class Example(val name: String, val description: String, val launcher: () -> Unit)

// ✨ Add new examples here - just add a new Example() entry!
private val EXAMPLES = listOf(
	Example("Ex_A", "Blue and white circles in diagonal pattern") { org.example.gameByBall2.ExA.main() },
	Example("Ex_B", "Random sized circles in horizontal line") { org.example.gameByBall2.ExB.main() },
	Example("Ex_C", "Bouncing ball in field") { org.example.gameByBall2.ExC.main() }
)

fun main() {
	val scanner = Scanner(System.`in`)

	while (true) {
		clearScreen()
		displayMenu()

		print("\nSelect an example (1-${EXAMPLES.size}) or press 'q' to quit: ")
		val input = scanner.nextLine().trim().lowercase()

		if (input == "q") {
			println("Goodbye!")
			break
		}

		try {
			val choice = input.toInt()
			if (choice in 1..EXAMPLES.size) {
				runExample(EXAMPLES[choice - 1])
			} else {
				println("\n❌ Invalid selection. Please enter 1-${EXAMPLES.size}.")
				pressAnyKeyToContinue(scanner)
			}
		} catch (e: NumberFormatException) {
			println("\n❌ Invalid input. Please enter a number.")
			pressAnyKeyToContinue(scanner)
		}
	}

	scanner.close()
}

private fun displayMenu() {
	println("╔════════════════════════════════════════════════════════╗")
	println("║         Processing Examples Launcher                   ║")
	println("╚════════════════════════════════════════════════════════╝")
	println()

	EXAMPLES.forEachIndexed { i, ex ->
		println("  [${i + 1}] ${ex.name}: ${ex.description}")
	}

	println()
	println("  [q] Quit")
}

private fun runExample(example: Example) {
	println("\n🚀 Launching ${example.name}...\n")

	try {
		example.launcher()
	} catch (e: Exception) {
		System.err.println("❌ Error running ${example.name}: ${e.message}")
		e.printStackTrace()
	}
}

private fun pressAnyKeyToContinue(scanner: Scanner) {
	print("\nPress Enter to continue...")
	scanner.nextLine()
}

private fun clearScreen() {
	try {
		val os = System.getProperty("os.name").lowercase()
		if (os.contains("win")) {
			ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor()
		} else {
			print("\u001b[H\u001b[2J")
			System.out.flush()
		}
	} catch (e: Exception) {
		println("\n\n\n\n\n\n\n\n\n\n")
	}
}
