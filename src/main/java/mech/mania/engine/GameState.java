package mech.mania.engine;

import com.fasterxml.jackson.databind.JsonNode;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.character.action.AttackActionType;
import mech.mania.engine.log.LogScores;
import mech.mania.engine.log.LogStats;
import mech.mania.engine.player.AttackInput;
import mech.mania.engine.player.MoveInput;
import mech.mania.engine.terrain.TerrainData;
import mech.mania.engine.terrain.TerrainState;
import mech.mania.engine.util.Position;
import mech.mania.engine.character.action.MoveAction;
import mech.mania.engine.log.Log;
import mech.mania.engine.player.Player;

import java.util.*;
import java.util.stream.Collectors;

import static mech.mania.engine.Config.*;

public class GameState {
    private Log log;
    private int turn;
    private final Map<String, CharacterState> characterStates;
    private final Map<String, TerrainState> terrainStates;
    private final Player humanPlayer;
    private final Player zombiePlayer;

    public GameState(Player human, Player zombie) {
        log = new Log();
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
            TerrainData terrainData = TERRAIN_DATAS.get(rand.nextInt(TERRAIN_DATAS.size()));

            TerrainState terrainState = new TerrainState(id, terrainData, position);
            terrainStates.put(id, terrainState);

            Map<String, JsonNode> diff = terrainState.diff(null);
            terrainStateDiffs.put(id, diff);
        }

        log.storeDiffs(characterStateDiffs, terrainStateDiffs);

        this.humanPlayer = human;
        this.zombiePlayer = zombie;
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
        Map<String, List<MoveAction>> possibleMoveActions = getPossibleMoveActions(player.isZombie());
        List<MoveAction> moveActions = player.getMoveInput(
                new MoveInput(possibleMoveActions, turn, characterStates, terrainStates)
        );

        // Reset existing stored actions
        applyClearActions(characterStates);

        // Apply move actions
        applyMoveActions(moveActions, possibleMoveActions);

        // Get player attack input
        Map<String, List<AttackAction>> possibleAttackActions = getPossibleAttackActions(player.isZombie());
        List<AttackAction> attackActions = player.getAttackInput(
                new AttackInput(possibleAttackActions, turn, characterStates, terrainStates)
        );

        // Apply attack actions
        applyAttackActions(attackActions, possibleAttackActions);

        // Decrement attack cooldowns and effects
        applyCooldownAndEffectDecay(player.isZombie());

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

        if (isFinished()) {
            log.storeResults(getScores(), getStats());
        }
    }

    public int getZombiesCount() {
        return (int) characterStates.values().stream().filter(CharacterState::isZombie).count();
    }
    public int getHumansCount() {
        return (int) characterStates.values().stream().filter(characterState -> !characterState.isZombie()).count();
    }

    public boolean isFinished() {
        if (getHumansCount() <= 0) {
            return true;
        }

        return turn >= TURNS;
    }

    public LogScores getScores() {
        int zombiesCount = getZombiesCount();
        int humansCount = getHumansCount();
        int humansInfected = zombiesCount - STARTING_ZOMBIES;
        int SCALE_FACTOR = 5;
        int humansScore = turn + (humansCount * SCALE_FACTOR);
        int zombiesScore = TURNS - turn + (humansInfected * SCALE_FACTOR);

        return new LogScores(humansScore, zombiesScore);
    }

    private LogStats getStats() {
        return new LogStats(turn, getHumansCount(), getZombiesCount());
    }

    private void applyClearActions(Map<String, CharacterState> characterStates) {
        characterStates.values().forEach(CharacterState::clearActions);
    }

    private void applyMoveActions(List<MoveAction> moveActions, Map<String, List<MoveAction>> possibleMoveActions) {
        for (String id : possibleMoveActions.keySet()) {
            List<MoveAction> possibleMoves = possibleMoveActions.get(id);
            List<MoveAction> attemptedMoves = moveActions.stream()
                    .filter(moveAction -> moveAction.getExecutingCharacterId().equals(id))
                    .toList();

            if (attemptedMoves.isEmpty()) {
                continue;
            }

            // Only register the first move
            MoveAction moveAction = attemptedMoves.get(0);

            // Check if possible
            boolean possible = false;
            for (MoveAction possibleMove : possibleMoves) {
                if (moveAction.getDestination().equals(possibleMove.getDestination())) {
                    possible = true;
                    break;
                }
            }

            if (!possible) {
                System.err.println(String.format("Invalid move action %s", moveAction));
                System.err.println(String.format("Allowed: %s", possibleMoves));
                continue;
            }


            // Apply move action
            characterStates.get(id).setPosition(moveAction.getDestination());
        }
    }
    private void applyAttackActions(List<AttackAction> attackActions, Map<String, List<AttackAction>> possibleAttackActions) {
        for (String id : possibleAttackActions.keySet()) {
            List<AttackAction> possibleAttacks = possibleAttackActions.get(id);
            List<AttackAction> attemptedMoves = attackActions.stream()
                    .filter(moveAction -> moveAction.getExecutingCharacterId().equals(id))
                    .toList();

            if (attemptedMoves.isEmpty()) {
                continue;
            }

            // Only register the first action
            AttackAction attackAction = attemptedMoves.get(0);

            // Check if possible
            boolean possible = false;
            for (AttackAction possibleMove : possibleAttacks) {
                if (attackAction.equals(possibleMove)) {
                    possible = true;
                    break;
                }
            }

            if (!possible) {
                System.err.println(String.format("Invalid move action %s", attackAction));
                System.err.println(String.format("Allowed: %s", possibleAttacks));
                continue;
            }

            // Apply attack action
            AttackActionType attackType = attackAction.getType();
            CharacterState executing = characterStates.get(id);
            String attackingId = attackAction.getAttackingId();
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
                }
            } else if (attackType == AttackActionType.TERRAIN) {
                // Handle terrain attacks
                TerrainState attacking = terrainStates.get(attackingId);

                attacking.attack();
            }
            executing.setAttackAction(attackAction);
            executing.resetAttackCooldownLeft();
        }
    }

    private Map<String, Position> getTilesInRange(Position start, int range, boolean isAttack) {
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
            boolean canTraverseThrough = true;

            boolean exists = terrainStates.containsKey(key);
            if (exists) {
                canTraverseThrough = false;
                TerrainState terrainState = terrainStates.get(key);
                if (terrainState.isDestroyed()) {
                    canTraverseThrough = true;
                }

                if (isAttack && terrainState.canAttackThrough()) {
                    canTraverseThrough = true;
                }
            }

            if (!canTraverseThrough) {
                continue;
            }

            // If not already added, add it
            if (!moves.containsKey(key)) {
                moves.put(key, newPosition);
            }

            // Recursively check for next moves
            Map<String, Position> fromThere = getTilesInRange(newPosition, range - 1, isAttack);

            fromThere.forEach((fromThereKey, fromThereNewPosition) -> {
                if (!moves.containsKey(fromThereKey)) {
                    moves.put(fromThereKey, fromThereNewPosition);
                }
            });
        }

        return moves;
    }

    private Map<String, List<MoveAction>> getPossibleMoveActions(boolean isZombie) {
        // Get controllable character states
        Map<String, CharacterState> controllableCharacterStates = new HashMap<>();

        for (CharacterState characterState : characterStates.values()) {
            if (characterState.isZombie() == isZombie) {
                controllableCharacterStates.put(characterState.getId(), characterState);
            }
        }

        // Get possible moves for each character
        Map<String, List<MoveAction>> possibleActions = new HashMap<>();

        for (CharacterState characterState : controllableCharacterStates.values()) {
            int range = characterState.canMove() ? characterState.getMoveSpeed() : 0;
            Map<String, Position> moves = getTilesInRange(characterState.getPosition(), range, false);

            possibleActions.put(characterState.getId(),
                    moves.values().stream()
                            .map(position -> new MoveAction(characterState.getId(), position))
                            .collect(Collectors.toList())
            );
        }

        return possibleActions;
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
            Map<String, Position> attackable = getTilesInRange(characterState.getPosition(), characterState.getAttackRange(), true);
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
                    // If the terrain is destroyed or not destroyable, we cannot attack it
                    if (terrainState.isDestroyed() || !terrainState.isDestroyable()) {
                        continue;
                    }

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
