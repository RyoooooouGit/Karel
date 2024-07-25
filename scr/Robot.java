package scr;

import javafx.scene.image.Image;

public class Robot extends Obstacle {
    int face, skinNum, rockInBag;
    Image[] faceImage = new Image[] {
            new Image("file:graph\\Robot_R.png"),
            new Image("file:graph\\Robot_U.png"),
            new Image("file:graph\\Robot_L.png"),
            new Image("file:graph\\Robot_D.png") };

    public Robot(int x, int y, int face) {
        this.x = x;
        this.y = y;
        this.face = face;
        rockInBag = 0;
    }

    public void move() {
        x = x + checkFace(0);
        y = y + checkFace(1);
    }

    public void turnLeft() {
        face = (face + 1) % 4;
    }

    public int checkFace(int xy) {
        int[][] checkFace = { { 1, 0, -1, 0 }, { 0, -1, 0, 1 } };
        return checkFace[xy][face];
    }
}