package org.example.gameByBall2;

import processing.core.PApplet;

public class Ex_A {
  public static class First extends PApplet {

    int count = 10;

    public void settings() {
      size((this.count + 1) * 100, (this.count + 1) * 100);
    }

    public void draw() {
      for (int i = 0; i < this.count; ++i) {
        if (i % 3 == 0) {
          fill(color(0, 0, 255));
        } else {
          fill(color(255, 255, 255));
        }
        ellipse(i * 100 + 100, i * 100 + 100, 100, 100);
      }
    }
  }

  public static class Third extends PApplet {
    int width = 5;
    int height = 5;

    public void settings() {
      size((this.width + 1) * 100, (this.height + 1) * 100);
    }

    public void draw() {
      int count = 0;
      for (int x = 0; x < this.width; ++x) {
        for (int y = 0; y < this.height; ++y) {
          if (count % 7 == 0 && count % 3 == 0) {
            fill(color(0, 255, 255));
          } else if (count % 7 == 0) {
            fill(color(0, 0, 255));
          } else if (count % 3 == 0) {
            fill(color(0, 255, 0));
          } else {
            fill(color(255, 255, 255));
          }
          ellipse(x * 100 + 100, y * 100 + 100, 80, 80);
          ++count;
        }
      }
    }
  }

  public static void main() {
    PApplet.main(Ex_A.First.class.getName());
    PApplet.main(Ex_A.Third.class.getName());
  }
}
