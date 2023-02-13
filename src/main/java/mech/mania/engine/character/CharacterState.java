package mech.mania.engine.character;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mech.mania.engine.util.Diffable;
import mech.mania.engine.util.Position;

import java.util.HashMap;
import java.util.Map;

// A current state of a character. Basically a character at a certain point in time.
public class CharacterState implements Cloneable, Diffable {
    private final String id;
    private Position position;
    private boolean isZombie;

    public CharacterState(String id, Position position, boolean isZombie) {
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
        try {
            return (CharacterState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, JsonNode> diff(Object previous) {
        if (this == previous) {
            return null;
        }
        CharacterState previousCharacterState = null;

        if (previous != null) {
            if (!(previous instanceof CharacterState cs)) {
                return null;
            }

            previousCharacterState = cs;

            if (!previousCharacterState.id.equals(id)) {
                return null;
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, JsonNode> diff = new HashMap<>();

        if (previousCharacterState == null || position != previousCharacterState.position) {
            diff.put("position", mapper.valueToTree(position));
        }

        if (previousCharacterState == null || isZombie != previousCharacterState.isZombie) {
            diff.put("isZombie", mapper.valueToTree(isZombie));
        }

        if (diff.isEmpty()) {
            return null;
        }

        return diff;
    }
}

