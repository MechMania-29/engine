package mech.mania.engine.character;

import static mech.mania.engine.Config.BOARD_SIZE;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean inBounds() {
        return this.x >= 0 && this.y >= 0 && this.x < BOARD_SIZE && this.y < BOARD_SIZE;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Position p)) {
            return false;
        }

        return ((this.getX() == p.getX()) && (this.getY() == p.getY()));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
