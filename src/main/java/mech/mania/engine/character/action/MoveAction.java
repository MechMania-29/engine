package mech.mania.engine.character.action;

import mech.mania.engine.character.Position;

public class MoveAction extends Action {
    private Position destination;

    public MoveAction(int executingCharacterId, Position destination) {
        super(executingCharacterId);

        this.destination = destination;
    }

    public Position getDestination() {
        return destination;
    }
}
