package mech.mania.engine.player;

import mech.mania.engine.GameState;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.util.Position;
import mech.mania.engine.character.action.MoveAction;

import java.util.*;

public class Player {
    private int port;
    private boolean isComputer;
    private boolean isZombie;

    public Player(int port, boolean isComputer, boolean isZombie) {
        this.port = port;
        this.isComputer = isComputer;
        this.isZombie = isZombie;
    }

    public boolean isZombie() {
        return isZombie;
    }

    public List<MoveAction> getMoveInput(Map<String, Map<String, Position>> possibleMoves) {
        List<MoveAction> moveActions;
        if (!isComputer) {
            //TODO: Connect to port and read in data, and we can send possibleMoves to help client
            moveActions = new ArrayList<>();
        } else {
            // Otherwise, for computer, pick one of the valid moves and just do it
            Random rand = new Random();

            moveActions = new ArrayList<>();

            for (String id : possibleMoves.keySet()) {
                Map<String, Position> possibleMovesForThisCharacter = possibleMoves.get(id);
                Position[] moves = possibleMovesForThisCharacter.values().toArray(new Position[0]);
                Position newPosition = moves[rand.nextInt(moves.length)];

                moveActions.add(new MoveAction(id, newPosition));
            }
        }

        return moveActions;
    }
}
