package mech.mania.engine;

import com.fasterxml.jackson.databind.JsonNode;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.terrain.TerrainState;
import mech.mania.engine.util.Position;
import mech.mania.engine.character.action.MoveAction;
import mech.mania.engine.log.Log;
import mech.mania.engine.log.LogSetupState;
import mech.mania.engine.player.Player;

import java.util.*;

import static mech.mania.engine.Config.*;

public class GameState implements Cloneable {
    private Log log;
    private int turn;
    private final Map<String, CharacterState> characterStates;
    private final Map<String, TerrainState> terrainStates;
    private final Player humanPlayer;
    private final Player zombiePlayer;

    public GameState() {
        log = new Log(new LogSetupState());
        turn = 0;
        characterStates = new HashMap<>();
        terrainStates = new HashMap<>();
        Map<String, Map<String, JsonNode>> characterStateDiffs = new HashMap<>();

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

            Map<String, JsonNode> diff = characterState.diff(null);
            characterStateDiffs.put(id, diff);
        }

        // TODO: Load terrain instead of randomly generating it
        Map<String, Map<String, JsonNode>> terrainStateDiffs = new HashMap<>();
        Random rand = new Random();
        for (int i = 0; i < 500; i++) {
            String id = Integer.toString(i);
            Position position = new Position(rand.nextInt(0, BOARD_SIZE), rand.nextInt(0, BOARD_SIZE));
            String imageId = TERRAIN_IMAGE_IDS.get(rand.nextInt(TERRAIN_IMAGE_IDS.size()));

            TerrainState terrainState = new TerrainState(id, imageId, position);
            terrainStates.put(id, terrainState);

            Map<String, JsonNode> diff = terrainState.diff(null);
            terrainStateDiffs.put(id, diff);
        }

        log.storeDiffs(characterStateDiffs, terrainStateDiffs);

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
        // Increment turn
        turn++;

        // Store character states for later
        Map<String, CharacterState> previousCharacterStates = new HashMap<>();
        for (CharacterState characterState : characterStates.values()) {
            previousCharacterStates.put(characterState.getId(), characterState.clone());
        }

        // Store terrain states for later
        Map<String, TerrainState> previousTerrainStates = new HashMap<>();
        for (TerrainState terrainState : terrainStates.values()) {
            previousTerrainStates.put(terrainState.getId(), terrainState.clone());
        }

        // Get player input
        List<MoveAction> moveActions = (turn % 2 == 1) ? zombiePlayer.getInput(this) : humanPlayer.getInput(this);

        // Apply move actions
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
        }

        // TODO: This is just for testing purposes and should be removed
        // Randomly pick some bits of terrain to destroy
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            TerrainState[] terrainStateValues = terrainStates.values().toArray(TerrainState[]::new);
            TerrainState toModify = terrainStateValues[rand.nextInt(terrainStateValues.length)];
            toModify.destroy();
        }

        // Store character diffs
        Map<String, Map<String, JsonNode>> characterStateDiffs = new HashMap<>();

        for (String id : characterStates.keySet()) {
            CharacterState previous = previousCharacterStates.get(id);
            CharacterState current = characterStates.get(id);

            Map<String, JsonNode> diff = current.diff(previous);

            if (diff == null) {
                continue;
            }

            characterStateDiffs.put(id, diff);
        }

        // Store terrain diffs
        Map<String, Map<String, JsonNode>> terrainStateDiffs = new HashMap<>();

        for (String id : terrainStates.keySet()) {
            TerrainState previous = previousTerrainStates.get(id);
            TerrainState current = terrainStates.get(id);

            Map<String, JsonNode> diff = current.diff(previous);

            if (diff == null) {
                continue;
            }

            terrainStateDiffs.put(id, diff);
        }

        log.storeDiffs(characterStateDiffs, terrainStateDiffs);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Character[][] board = new Character[BOARD_SIZE][BOARD_SIZE];

        for (Character[] row : board) {
            Arrays.fill(row, '-');
        }

        for (TerrainState terrainState : terrainStates.values()) {
            if (terrainState.isDestroyed()) {
                continue;
            }
            Position position = terrainState.getPosition();
            board[position.getY()][position.getX()] = terrainState.getImageId().charAt(0);
        }

        for (CharacterState characterState : characterStates.values()) {
            Position position = characterState.getPosition();
            board[position.getY()][position.getX()] =
                    (characterState.isZombie()) ? 'Z' : 'H';
        }

        sb.append("GameState{\n\t");

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
