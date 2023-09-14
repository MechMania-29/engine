package mech.mania.engine.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static mech.mania.engine.Config.BOARD_SIZE;

public class Position implements Cloneable {
    @JsonProperty("x")
    private int x;
    @JsonProperty("y")
    private int y;

    @JsonCreator
    public Position(@JsonProperty("x") int x, @JsonProperty("y") int y) {
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

    public void add(Position other) {
        this.x += other.x;
        this.y += other.y;
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

    @Override
    public Position clone() {
        try {
            return (Position) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
