package mech.mania.engine.player.action;

import mech.mania.engine.player.Position;

public class MoveAction extends Action {
    private Position destination;

    public MoveAction(int executingPlayerId, Position destination) {
        super(executingPlayerId);

        this.destination = destination;
    }

    public Position getDestination() {
        return destination;
    }
}
