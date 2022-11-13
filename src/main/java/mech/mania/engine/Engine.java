package mech.mania.engine;

import mech.mania.engine.player.Position;
import mech.mania.engine.player.action.MoveAction;

public class Engine {
    public static void main(String[] args) {
        MoveAction testAction = new MoveAction(0, new Position(132, 13));
        System.out.println(testAction.getExecutingPlayerId() + " moving to " + testAction.getDestination());
    }
}