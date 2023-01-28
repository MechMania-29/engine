package mech.mania.engine.character;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import mech.mania.engine.log.NullBooleanFilter;

// A current state of a character. Basically a character at a certain point in time.
public class CharacterState implements Cloneable {
    @JsonIgnore
    private final String id;
    @JsonProperty("position") @JsonInclude(JsonInclude.Include.NON_NULL)
    private Position position;
    @JsonProperty("isZombie") @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = NullBooleanFilter.class)
    private Boolean isZombie;

    public CharacterState(String id, Position position, Boolean isZombie) {
        this.id = id;
        this.position = position;
        this.isZombie = isZombie;
    }

    public String getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    @JsonIgnore
    public Boolean isZombie() {
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
        try {
            return (CharacterState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

