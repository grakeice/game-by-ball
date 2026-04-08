package org.example.gameByBall2;

import java.util.Arrays;

import processing.core.PApplet;

public class Ex_B {
  public static class Main extends PApplet {

    public void settings() {
      size(1000, 100);
      noLoop();
    }

    public void draw() {
      var array = new int[10];
      var rad = Arrays.stream(array).map(n -> (int) random(10, 100)).toArray();
      int count = 0;
      for (var r : rad) {
        ellipse(count * 100 + 50, 50, r, r);
        count++;
      }
    }
  }

  public static void main() {
    PApplet.main(Main.class.getName());
  }
}
