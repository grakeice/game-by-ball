package org.example;

import java.util.List;
import java.util.Scanner;

class App {

  // Record for Example definition
  record Example(String name, String description, Runnable launcher) {
  }

  // ✨ Add new examples here - just add a new Example() entry!
  private static final List<Example> EXAMPLES = List.of(
      new Example("Ex_A", "Blue and white circles in diagonal pattern", org.example.gameByBall2.Ex_A::main),
      new Example("Ex_B", "Random sized circles in horizontal line", org.example.gameByBall2.Ex_B::main),
      new Example("Ex_C", "Bouncing ball in field", org.example.gameByBall2.Ex_C::main));

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    while (true) {
      clearScreen();
      displayMenu();

      System.out.print("\nSelect an example (1-" + EXAMPLES.size() + ") or press 'q' to quit: ");
      String input = scanner.nextLine().trim().toLowerCase();

      if (input.equals("q")) {
        System.out.println("Goodbye!");
        break;
      }

      try {
        int choice = Integer.parseInt(input);
        if (choice >= 1 && choice <= EXAMPLES.size()) {
          runExample(EXAMPLES.get(choice - 1));
        } else {
          System.out.println("\n❌ Invalid selection. Please enter 1-" + EXAMPLES.size() + ".");
          pressAnyKeyToContinue(scanner);
        }
      } catch (NumberFormatException e) {
        System.out.println("\n❌ Invalid input. Please enter a number.");
        pressAnyKeyToContinue(scanner);
      }
    }

    scanner.close();
  }

  private static void displayMenu() {
    System.out.println("╔════════════════════════════════════════════════════════╗");
    System.out.println("║         Processing Examples Launcher                   ║");
    System.out.println("╚════════════════════════════════════════════════════════╝");
    System.out.println();

    for (int i = 0; i < EXAMPLES.size(); i++) {
      Example ex = EXAMPLES.get(i);
      System.out.printf("  [%d] %s: %s%n", i + 1, ex.name(), ex.description());
    }

    System.out.println();
    System.out.println("  [q] Quit");
  }

  private static void runExample(Example example) {
    System.out.println("\n🚀 Launching " + example.name() + "...\n");

    try {
      example.launcher().run();
    } catch (Exception e) {
      System.err.println("❌ Error running " + example.name() + ": " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void pressAnyKeyToContinue(Scanner scanner) {
    System.out.print("\nPress Enter to continue...");
    scanner.nextLine();
  }

  private static void clearScreen() {
    try {
      String os = System.getProperty("os.name").toLowerCase();
      if (os.contains("win")) {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
      } else {
        System.out.print("\033[H\033[2J");
        System.out.flush();
      }
    } catch (Exception e) {
      System.out.println("\n\n\n\n\n\n\n\n\n\n");
    }
  }
}
