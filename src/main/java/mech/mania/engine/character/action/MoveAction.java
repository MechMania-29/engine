package mech.mania.engine.character.action;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import mech.mania.engine.util.Position;

public class MoveAction extends Action {
    private final Position destination;

    @JsonCreator
    public MoveAction(@JsonProperty("executingCharacterId") String executingCharacterId,
                      @JsonProperty("destination") Position destination) {
        super(executingCharacterId);

        this.destination = destination;
    }

    public Position getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "MoveAction{" +
                "executingCharacterId=" + this.getExecutingCharacterId() + ", " +
                "destination=" + destination +
                '}';
    }
}
