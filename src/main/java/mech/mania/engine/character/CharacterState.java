package mech.mania.engine.character;

// A current state of a character. Basically a character at a certain point in time.
public class CharacterState implements Cloneable {
    private int id;
    private Position position;
    private boolean isZombie = false;

    public CharacterState(int id, Position position, boolean isZombie) {
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
    public CharacterState clone() {
        return new CharacterState(id, position, isZombie);
    }
}

