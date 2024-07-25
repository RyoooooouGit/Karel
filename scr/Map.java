package scr;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Map {
  Robot karel;
  int length, width;
  int trapAmount, rockAmount, wallAmount, rockLeft;
  Obstacle[] trap, rock, wall;
  ArrayList<Obstacle> obstacle = new ArrayList<>();
  double mapElementWidth;
  String logOutput, wrongOutput;

  public Map(int mapNum) {
    logOutput = "";
    wrongOutput = "";
    length = 10;
    width = 10;
    switch (mapNum) {
      case 0:
        length = 6;
        width = 3;
        karel = new Robot(0, 1, 0);
        rock = new Rock[] { new Rock(5, 1) };
        break;
      case 1:
        length = 6;
        width = 3;
        karel = new Robot(0, 2, 0);
        rock = new Rock[] { new Rock(5, 1) };
        wall = new Wall[] { new Wall(0, 0), new Wall(0, 1), new Wall(2, 2),
            new Wall(3, 2), new Wall(4, 2), new Wall(5, 2) };
        break;
      case 2:
        length = 8;
        width = 5;
        karel = new Robot(0, 4, 0);
        trap = new Trap[] { new Trap(4, 2) };
        rock = new Rock[] { new Rock(2, 1), new Rock(7, 0) };
        wall = new Wall[] { new Wall(4, 0), new Wall(4, 1), new Wall(4, 3), new Wall(4, 4),
            new Wall(5, 3), new Wall(5, 4), new Wall(6, 3), new Wall(6, 4),
            new Wall(7, 3), new Wall(7, 4) };
        break;
    }
    setIntoObstacle();
    mapElementWidth = getElementWidth();
  }

  public void setIntoObstacle() {
    if (trap != null) {
      obstacle.addAll(Arrays.asList(trap));
      this.trapAmount = trap.length;
    }
    if (rock != null) {
      obstacle.addAll(Arrays.asList(rock));
      this.rockAmount = rock.length;
      this.rockLeft = rock.length;
    }
    if (wall != null) {
      obstacle.addAll(Arrays.asList(wall));
      this.wallAmount = wall.length;
    }
  }

  public GridPane output() {
    GridPane map = new GridPane();
    map.setPadding(new Insets(0, 0, 0, 0));
    map.setGridLinesVisible(true);
    map.setHgap(0.3);
    map.setVgap(0.3);
    Image rock = new Image("file:graph\\Rock.png");
    Image wall = new Image("file:graph\\Wall.png");
    Image trap = new Image("file:graph\\Trap.png");
    Image trapFilled = new Image("file:graph\\Trap_Filled.png");
    Image ground = new Image("file:graph\\Ground.png");
    for (int i = 0; i < length; i++) {
      for (int j = 0; j < width; j++) {
        ImageView groundImageView = new ImageView(ground);
        groundImageView.setFitHeight(mapElementWidth);
        groundImageView.setFitWidth(mapElementWidth);
        map.add(groundImageView, i, j);
      }
    }
    for (int i = 0; i < rockAmount + wallAmount + trapAmount; i++) {
      ImageView mapImageView = null;
      if (obstacle.get(i) instanceof Rock) {
        if (((Rock) obstacle.get(i)).ifPicked) {
          mapImageView = new ImageView(ground);
        } else {
          mapImageView = new ImageView(rock);
        }
      } else if (obstacle.get(i) instanceof Wall) {
        mapImageView = new ImageView(wall);
      } else if (obstacle.get(i) instanceof Trap) {
        if (((Trap) obstacle.get(i)).ifFilled) {
          mapImageView = new ImageView(trapFilled);
        } else {
          mapImageView = new ImageView(trap);
        }
      }
      mapImageView.setFitHeight(mapElementWidth);
      mapImageView.setFitWidth(mapElementWidth);
      map.add(mapImageView, obstacle.get(i).x, obstacle.get(i).y);
    }
    ImageView robotImageView = new ImageView(karel.faceImage[karel.face]);
    robotImageView.setFitHeight(mapElementWidth);
    robotImageView.setFitWidth(mapElementWidth);
    map.add(robotImageView, karel.x, karel.y);
    return map;
  }

  private Obstacle front() {
    if (karel.x + karel.checkFace(0) == -1 || karel.x + karel.checkFace(0) == length
        || karel.y + karel.checkFace(1) == -1 || karel.y + karel.checkFace(1) == width) {
      return new Edge();
    }
    for (int i = 0; i < rockAmount + wallAmount + trapAmount; i++) {
      if (obstacle.get(i).x == karel.x + karel.checkFace(0)
          && obstacle.get(i).y == karel.y + karel.checkFace(1)) {
        return obstacle.get(i);
      }
    }
    return new Obstacle();
  }

  public boolean checkIfHit() {
    Obstacle front = front();
    String obstacleStr = "";
    if (front instanceof Rock && !((Rock) front).ifPicked) {
      obstacleStr = "a rock";
    } else if (front instanceof Wall) {
      obstacleStr = "a wall";
    } else if (front instanceof Edge) {
      obstacleStr = "the edge";
    }
    if (!obstacleStr.equals("")) {
      wrongOutput += "You are hitting into " + obstacleStr + "!\n";
      return true;
    }
    return false;
  }

  public boolean checkIfTrap() {
    Obstacle front = front();
    if (front instanceof Trap && !((Trap) front).ifFilled)
      return true;
    else
      return false;
  }

  public boolean checkIfRock() {
    Obstacle front = front();
    if (front instanceof Rock && !((Rock) front).ifPicked)
      return true;
    else
      return false;
  }

  public void pickRock() {
    boolean pickSuccess = false, trapExist = false;
    Obstacle front = front();
    if (front instanceof Rock && !((Rock) front).ifPicked) {
      ((Rock) front).ifPicked = true;
      rockLeft--;
      karel.rockInBag++;
      pickSuccess = true;
    } else if (front instanceof Trap && ((Trap) front).ifFilled) {
      trapExist = true;
    }
    if (pickSuccess) {
      logOutput += "You have got a rock!\n";
      logOutput += "Now you have " + karel.rockInBag + " in your bag.\n";
    } else if (trapExist) {
      wrongOutput += "There rock ahead is in a trap!\nPlease enter again.\n";
    } else {
      wrongOutput += "There is no rock ahead!\nPlease enter again.\n";
    }
  }

  public void showInformation() {
    int minDistance = length + width - 2;
    for (int i = 0; i < rockAmount + wallAmount + trapAmount; i++) {
      if (obstacle.get(i) instanceof Rock && !((Rock) obstacle.get(i)).ifPicked) {
        int distance = Math.abs(obstacle.get(i).x - karel.x) + Math.abs(obstacle.get(i).y - karel.y);
        minDistance = Math.min(minDistance, distance);
      }
    }
    logOutput += "There is " + rockLeft + " rock" + ifMoreThanOne(rockLeft) + " on the map that you need to collect.\n";
    logOutput += "You have " + ifNone(karel.rockInBag) + " rock" + ifMoreThanOne(karel.rockInBag) + " in your bag.\n";
    logOutput += "You are " + minDistance + " step" + ifMoreThanOne(minDistance) + " away from the nearest rock.\n";
  }

  public void putRock() {
    if (karel.rockInBag == 0) {
      wrongOutput += "You don't have any rock in your bag now.\n";
    } else {
      boolean putSuccess = false;
      boolean trapExist = false;
      Obstacle front = front();
      if (front instanceof Trap) {
        trapExist = true;
        if (!((Trap) front).ifFilled) {
          ((Trap) front).ifFilled = true;
          karel.rockInBag--;
          putSuccess = true;
        }
      }
      if (putSuccess) {
        logOutput += "You have put down a rock.\n";
        logOutput += "Now you have " + karel.rockInBag + " left.\n";
      } else if (trapExist) {
        wrongOutput += "The trap in front of you has been filled.\n";
      } else {
        wrongOutput += "The ground in front of you is not an unfilled trap.\n";
      }
    }
  }

  private String ifNone(int x) {
    return x > 0 ? x + "" : "no";
  }

  private String ifMoreThanOne(int x) {
    return x > 1 ? "s" : "";
  }

  public Map cloneMap() {
    Map map = new Map(3);
    map.length = this.length;
    map.width = this.width;
    map.mapElementWidth = this.mapElementWidth;
    if (karel != null) {
      map.karel = new Robot(this.karel.x, this.karel.y, this.karel.face);
    }
    if (trap != null) {
      map.trap = new Trap[this.trap.length];
    }
    if (rock != null) {
      map.rock = new Rock[this.rock.length];
    }
    if (wall != null) {
      map.wall = new Wall[this.wall.length];
    }
    map.setIntoObstacle();
    for (int i = 0; i < this.trapAmount + this.rockAmount + this.wallAmount; i++) {
      if (this.obstacle.get(i) instanceof Trap) {
        map.obstacle.set(i, new Trap(this.obstacle.get(i).x, this.obstacle.get(i).y));
        ((Trap) map.obstacle.get(i)).ifFilled = ((Trap) this.obstacle.get(i)).ifFilled;
      } else if (this.obstacle.get(i) instanceof Rock) {
        map.obstacle.set(i, new Rock(this.obstacle.get(i).x, this.obstacle.get(i).y));
        ((Rock) map.obstacle.get(i)).ifPicked = ((Rock) this.obstacle.get(i)).ifPicked;
      } else if (this.obstacle.get(i) instanceof Wall) {
        map.obstacle.set(i, new Wall(this.obstacle.get(i).x, this.obstacle.get(i).y));
      }
    }
    return map;
  }

  public double getElementWidth() {
    return (620 / length) < (500 / width) ? (620 / length) : (500 / width);
  }
}