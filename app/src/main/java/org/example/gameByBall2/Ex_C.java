package org.example.gameByBall2;

import java.util.Arrays;

import processing.core.PApplet;

public class Ex_C {

  public static record Coordinate(float x, float y) {
  }

  public static class Shape {
    float radius;
    Coordinate position = new Coordinate(0, 0);
    Coordinate speed = new Coordinate(0, 0);

    Shape(float radius, Coordinate position, Coordinate speed) {
      this.radius = radius;
      this.position = position;
      this.speed = speed;
    }

    public void setPosition(Coordinate position) {
      this.position = position;
    }

    public void setSpeed(Coordinate speed) {
      this.speed = speed;
    }
  }

  public static class Field {
    int x = 500;
    int y = 500;
    Shape[] shapes = { new Shape(50, new Coordinate(50, 50), new Coordinate(5, 2)) };

    public void addShape(Shape shape) {
      this.shapes = Arrays.copyOf(this.shapes, this.shapes.length + 1);
      this.shapes[this.shapes.length - 1] = shape;
    }

    public void compute() {
      for (var shape : shapes) {
        float vx = shape.speed.x;
        float vy = shape.speed.y;
        float nx = shape.position.x + vx;
        float ny = shape.position.y + vy;

        if (nx - shape.radius < 0 || nx + shape.radius > this.x) {
          vx = -vx;
          nx = shape.position.x + vx;
        }

        if (ny - shape.radius < 0 || ny + shape.radius > this.y) {
          vy = -vy;
          ny = shape.position.y + vy;
        }

        shape.setSpeed(new Coordinate(vx, vy));
        shape.setPosition(new Coordinate(nx, ny));
      }
    }

  }

  public static class Main extends PApplet {
    Field field;

    public void settings() {
      this.field = new Field();
      size(this.field.x, this.field.y);
    }

    public void draw() {
      background(color(255, 255, 255));
      for (var shape : this.field.shapes) {
        ellipse(shape.position.x, shape.position.y, shape.radius, shape.radius);
      }
      this.field.compute();
    }
  }

  public static void main() {
    PApplet.main(Main.class.getName());
  }
}
