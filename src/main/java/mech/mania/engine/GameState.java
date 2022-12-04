package mech.mania.engine;

import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.Position;
import mech.mania.engine.character.action.MoveAction;

import java.util.Arrays;
import java.util.List;

import static mech.mania.engine.Config.*;

public class GameState implements Cloneable {
    private final List<CharacterState> characterStates;

    public GameState() {
        characterStates = Arrays.asList(new CharacterState[TOTAL_CHARACTERS]);

        for (int i = 0; i < TOTAL_CHARACTERS; i++) {
            boolean isZombie = i < STARTING_ZOMBIES;
            Position startingPosition;

            if (isZombie) {
                startingPosition = new Position(BOARD_SIZE / 2 + i, 0);
            } else {
                startingPosition = new Position(BOARD_SIZE / 2 + i, BOARD_SIZE - 1);
            }

            characterStates.set(i, new CharacterState(i, startingPosition, isZombie));
        }
    };

    public List<CharacterState> getCharacterStates() {
        return characterStates;
    }

    public void runTurn(List<MoveAction> moveActions) {
        for (MoveAction moveAction : moveActions) {
            int id = moveAction.getExecutingCharacterId();

            if (id < 0 || id >= TOTAL_CHARACTERS) {
                continue;
            }

            Position destination = moveAction.getDestination();
            if (!destination.inBounds()) {
                continue;
            }

            // TODO: Distance check

            characterStates.get(id).setPosition(destination);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Character[][] board = new Character[BOARD_SIZE][BOARD_SIZE];

        for (Character[] row : board) {
            Arrays.fill(row, '-');
        }

        for (CharacterState characterState : characterStates) {
            Position position = characterState.getPosition();
            board[position.getY()][position.getX()] =
                    (characterState.isZombie()) ? 'Z' : 'H';
        }

        sb.append("CharacterState{\n\t");

        for (int i = 0; i < BOARD_SIZE; i++) {
            if (i != 0) {
                sb.append("\n\t");
            }
            for (int j = 0; j < BOARD_SIZE; j++) {
                sb.append(board[i][j]);
            }
        }

        sb.append("\n}");

        return sb.toString();
    }

    @Override
    public GameState clone() {
        try {
            return (GameState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
