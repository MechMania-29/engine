package mech.mania.engine;

import com.fasterxml.jackson.databind.JsonNode;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.character.action.AttackActionType;
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

        // Figure out whose turn it is
        Player player = (turn % 2 == 1) ? zombiePlayer : humanPlayer;

        // Get player move input
        Map<String, Map<String, Position>> possibleMoves = getPossibleMovePositions(player.isZombie());
        List<MoveAction> moveActions = player.getMoveInput(possibleMoves, characterStates);

        // Apply move actions
        applyMoveActions(moveActions, possibleMoves);

        // Get player attack input
        Map<String, List<AttackAction>> possibleAttackActions = getPossibleAttackActions(player.isZombie());
        List<AttackAction> attackActions = player.getAttackInput(possibleAttackActions);

        // Apply attack actions
        applyAttackActions(attackActions, possibleAttackActions);

        // Decrement attack cooldowns and effects
        applyCooldownAndEffectDecay(player.isZombie());

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

    private void applyMoveActions(List<MoveAction> moveActions, Map<String, Map<String, Position>> possibleMoves) {
        for (MoveAction moveAction : moveActions) {
            String id = moveAction.getExecutingCharacterId();
            Position destination = moveAction.getDestination();
            String destinationKey = destination.toString();

            // Ignore if they can't move this character
            if (!possibleMoves.containsKey(id)) {
                continue;
            }

            // Ignore if it's not a possible move
            Map<String, Position> possibleMovesForThisCharacter = possibleMoves.get(id);
            if (!possibleMovesForThisCharacter.containsKey(destinationKey)) {
                continue;
            }

            // Apply move action
            characterStates.get(id).setPosition(destination);
        }
    }
    private void applyAttackActions(List<AttackAction> attackActions, Map<String, List<AttackAction>> possibleAttackActions) {
        for (AttackAction attackAction : attackActions) {
            String id = attackAction.getExecutingCharacterId();
            CharacterState executing = characterStates.get(id);
            String attackingId = attackAction.getAttackingId();
            AttackActionType attackType = attackAction.getType();

            // Ignore if they can't use this character
            if (!possibleAttackActions.containsKey(id)) {
                continue;
            }

            // Ignore if it's not a possible move
            boolean isPossible = false;

            for (AttackAction possible : possibleAttackActions.get(id)) {
                if (attackAction == possible) {
                    isPossible = true;
                    break;
                }
            }

            if (!isPossible) {
                continue;
            }

            if (attackType == AttackActionType.CHARACTER) {
                // Handle character attacks
                CharacterState attacking = characterStates.get(attackingId);
                if (executing.isZombie()) {
                    // No attacking zombies as a zombie
                    if (attacking.isZombie()) {
                        continue;
                    }
                    attacking.setHealth(attacking.getHealth() - 1);

                    if (attacking.getHealth() == 0) {
                        attacking.makeZombie();
                    }
                } else {
                    // No attacking humans as a human
                    if (!attacking.isZombie()) {
                        continue;
                    }

                    attacking.stun();

                    executing.resetAttackCooldownLeft();
                }
            } else if (attackType == AttackActionType.TERRAIN) {
                // Handle terrain attacks
                TerrainState attacking = terrainStates.get(attackingId);

                attacking.attack();
            }
        }
    }

    private Map<String, Position> getTilesInRange(Position start, int range) {
        Map<String, Position> moves = new HashMap<>();

        if (range <= 0) {
            moves.put(start.toString(), start.clone());
            return moves;
        }

        for (Position direction : DIRECTIONS) {
            Position newPosition = start.clone();
            newPosition.add(direction);
            String key = newPosition.toString();

            // Check in bounds
            if (!newPosition.inBounds()) {
                continue;
            }

            // Check not terrain
            if (terrainStates.containsKey(key) && !terrainStates.get(key).isDestroyed()) {
                continue;
            }

            // If not already added, add it
            if (!moves.containsKey(key)) {
                moves.put(key, newPosition);
            }

            // Recursively check for next moves
            Map<String, Position> fromThere = getTilesInRange(newPosition, range - 1);

            fromThere.forEach((fromThereKey, fromThereNewPosition) -> {
                if (!moves.containsKey(fromThereKey)) {
                    moves.put(fromThereKey, fromThereNewPosition);
                }
            });
        }

        return moves;
    }

    private Map<String, Map<String, Position>> getPossibleMovePositions(boolean isZombie) {
        // Get controllable character states
        Map<String, CharacterState> controllableCharacterStates = new HashMap<>();

        for (CharacterState characterState : characterStates.values()) {
            if (characterState.isZombie() == isZombie) {
                controllableCharacterStates.put(characterState.getId(), characterState);
            }
        }

        // Get possible moves for each character
        Map<String, Map<String, Position>> possibleMoves = new HashMap<>();

        for (CharacterState characterState : controllableCharacterStates.values()) {
            int range = characterState.canMove() ? characterState.getMoveSpeed() : 0;
            Map<String, Position> moves = getTilesInRange(characterState.getPosition(), range);

            possibleMoves.put(characterState.getId(), moves);
        }

        return possibleMoves;
    }
    private Map<String, List<AttackAction>> getPossibleAttackActions(boolean isZombie) {
        // Get controllable character states
        Map<String, CharacterState> controllableCharacterStates = new HashMap<>();

        for (CharacterState characterState : characterStates.values()) {
            if (characterState.isZombie() == isZombie) {
                controllableCharacterStates.put(characterState.getId(), characterState);
            }
        }

        // Get possible attack actions for each character
        Map<String, List<AttackAction>> possibleAttackActions = new HashMap<>();

        for (CharacterState characterState : controllableCharacterStates.values()) {
            Map<String, Position> attackable = getTilesInRange(characterState.getPosition(), characterState.getAttackRange());
            List<AttackAction> attackActions = new ArrayList<>();

            if (characterState.canAttack()) {
                // Handle attackable enemies
                for (CharacterState otherCharacterState : characterStates.values()) {
                    // If is on our team, we don't attack them
                    if (otherCharacterState.isZombie() == isZombie) {
                        continue;
                    }

                    // If they are not within attackable range, we cannot attack them
                    if (!attackable.containsKey(otherCharacterState.getPosition().toString())) {
                        continue;
                    }

                    // We can attack them
                    attackActions.add(new AttackAction(characterState.getId(), otherCharacterState.getId(), AttackActionType.CHARACTER));
                }

                // Handle attackable terrain
                for (TerrainState terrainState : terrainStates.values()) {
                    // If they are not within attackable range, we cannot attack it
                    if (!attackable.containsKey(terrainState.getPosition().toString())) {
                        continue;
                    }

                    // We can attack them
                    attackActions.add(new AttackAction(characterState.getId(), terrainState.getId(), AttackActionType.TERRAIN));
                }
            }

            // Add all attack actions for character
            possibleAttackActions.put(characterState.getId(), attackActions);
        }

        return possibleAttackActions;
    }

    private void applyCooldownAndEffectDecay(boolean isZombie) {
        for (CharacterState character : characterStates.values()) {
            if (character.isZombie() == isZombie) {
                character.applyCooldownAndEffectDecay();
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
}
