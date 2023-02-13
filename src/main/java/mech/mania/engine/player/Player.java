package mech.mania.engine.player;

import mech.mania.engine.GameState;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.util.Position;
import mech.mania.engine.character.action.MoveAction;

import java.util.*;

import static mech.mania.engine.Config.*;

public class Player {
    private int port;
    private boolean isComputer;
    private boolean isZombie;

    public Player(int port, boolean isComputer, boolean isZombie) {
        this.port = port;
        this.isComputer = isComputer;
        this.isZombie = isZombie;
    }

    public List<MoveAction> getInput(GameState gameState) {
        Map<String, CharacterState> allCharacterStates = gameState.getCharacterStates();

        // Get controllable character states
        Map<String, CharacterState> controllableCharacterStates = new HashMap<>();

        for (CharacterState characterState : allCharacterStates.values()) {
            if (characterState.isZombie() == isZombie) {
                controllableCharacterStates.put(characterState.getId(), characterState);
            }
        }

        // Get possible moves for each character
        Map<String, Map<String, Position>> possibleMoves = new HashMap<>();

        for (CharacterState characterState : controllableCharacterStates.values()) {
            Map<String, Position> moves = new HashMap<>();

            for (Position direction : DIRECTIONS) {
                Position newPosition = characterState.getPosition().clone();
                newPosition.add(direction);

                moves.put(newPosition.toString(), newPosition);
            }

            possibleMoves.put(characterState.getId(), moves);
        }

        // Read in move actions
        List<MoveAction> moveActions;
        if (!isComputer) {
            //TODO: Connect to port and read in data
            moveActions = new ArrayList<>();
        } else {
            Random rand = new Random();

            moveActions = new ArrayList<>();

            for (CharacterState characterState : controllableCharacterStates.values()) {
                if (rand.nextInt(5) == 0) {
                    Position[] moves = possibleMoves.get(characterState.getId()).values().toArray(new Position[0]);
                    Position newPosition = moves[rand.nextInt(moves.length)];

                    moveActions.add(new MoveAction(characterState.getId(), newPosition));
                }
            }
        }

        // TODO: Validate moves

        return moveActions;
    }
}
