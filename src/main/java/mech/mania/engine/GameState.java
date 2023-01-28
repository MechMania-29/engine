package mech.mania.engine;

import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.Position;
import mech.mania.engine.character.action.MoveAction;
import mech.mania.engine.log.Log;
import mech.mania.engine.log.LogSetupState;
import mech.mania.engine.player.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mech.mania.engine.Config.*;

public class GameState implements Cloneable {
    private Log log;
    private int turn;
    private final Map<String, CharacterState> characterStates;
    private final Player humanPlayer;
    private final Player zombiePlayer;

    public GameState() {
        log = new Log(new LogSetupState());
        turn = 0;
        characterStates = new HashMap<>();
        Map<String, CharacterState> modifiedCharacterStates = log.getTurnStates().get(turn).getModifiedCharacterStates();

        for (int i = 0; i < TOTAL_CHARACTERS; i++) {
            String id = Integer.toString(i);
            boolean isZombie = i < STARTING_ZOMBIES;
            Position startingPosition;

            if (isZombie) {
                startingPosition = new Position(BOARD_SIZE / 2 + i, 0);
            } else {
                startingPosition = new Position(BOARD_SIZE / 2 + i, BOARD_SIZE - 1);
            }

            CharacterState characterState = new CharacterState(id, startingPosition, isZombie);
            characterStates.put(id, characterState);
            modifiedCharacterStates.put(id, characterState.clone());
        }

        humanPlayer = new Player(-1, true, false);
        zombiePlayer = new Player(-1, true, true);
    };

    public Map<String, CharacterState> getCharacterStates() {
        return characterStates;
    }

    public Log getLog() {
        return log;
    }

    public int getTurn() {
        return turn;
    }

    public void runTurn() {
        turn++;

        List<MoveAction> moveActions = (turn % 2 == 1) ? zombiePlayer.getInput(this) : humanPlayer.getInput(this);

        Map<String, CharacterState> modifiedCharacterStates = log.getTurnStates().get(turn).getModifiedCharacterStates();
        for (MoveAction moveAction : moveActions) {
            String id = moveAction.getExecutingCharacterId();

            if (!characterStates.containsKey(id)) {
                continue;
            }

            Position destination = moveAction.getDestination();
            if (!destination.inBounds()) {
                continue;
            }

            // TODO: Distance check

            characterStates.get(id).setPosition(destination);

            CharacterState existing = modifiedCharacterStates.get(id);
            if (existing == null) {
                modifiedCharacterStates.put(id, new CharacterState(id, destination.clone(), null));
            } else {
                existing.setPosition(destination.clone());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Character[][] board = new Character[BOARD_SIZE][BOARD_SIZE];

        for (Character[] row : board) {
            Arrays.fill(row, '-');
        }

        for (CharacterState characterState : characterStates.values()) {
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
