package mech.mania.engine.player;

// A current state of a player. Basically a player at a certain point in time.
public class PlayerState implements Cloneable {
    private int id;
    private Position position;
    private boolean isZombie = false;

    public PlayerState(int id, Position position, boolean isZombie) {
        this.id = id;
        this.position = position;
        this.isZombie = isZombie;
    }

    public int getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isZombie() {
        return isZombie;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void makeZombie() {
        isZombie = true;
    }

    @Override
    public PlayerState clone() {
        return new PlayerState(id, position, isZombie);
    }
}

