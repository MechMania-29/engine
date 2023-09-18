package mech.mania.engine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mech.mania.engine.character.CharacterClassAbility;
import mech.mania.engine.character.CharacterClassType;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.action.*;
import mech.mania.engine.log.LogErrors;
import mech.mania.engine.log.LogScores;
import mech.mania.engine.log.LogStats;
import mech.mania.engine.player.*;
import mech.mania.engine.player.input.AbilityInput;
import mech.mania.engine.player.input.AttackInput;
import mech.mania.engine.player.input.ChooseClassesInput;
import mech.mania.engine.player.input.MoveInput;
import mech.mania.engine.terrain.TerrainData;
import mech.mania.engine.terrain.TerrainState;
import mech.mania.engine.terrain.TerrainType;
import mech.mania.engine.util.Position;
import mech.mania.engine.log.Log;
import mech.mania.engine.util.SpreadMap;

import java.io.File;
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

    public GameState(Player human, Player zombie, List<List<Character>> map) {
        log = new Log();
        turn = 0;
        characterStates = new HashMap<>();
        terrainStates = new HashMap<>();
        Map<String, Map<String, JsonNode>> characterStateDiffs = new HashMap<>();

        // Create characters
        for (int i = 0; i < TOTAL_CHARACTERS; i++) {
            String id = Integer.toString(i);
            boolean isZombie = i < STARTING_ZOMBIES;
            Position startingPosition;

            if (isZombie) {
                startingPosition = new Position(BOARD_SIZE / 2 + i, 0);
            } else {
                startingPosition = new Position(BOARD_SIZE / 2 + i, BOARD_SIZE - 1);
            }

            CharacterClassType classType = isZombie ? CharacterClassType.ZOMBIE : CharacterClassType.NORMAL;
            CharacterState characterState = new CharacterState(id, startingPosition, isZombie, classType);
            characterStates.put(id, characterState);
        }

        // Apply human classes
        ChooseClassesInput chooseClassesInput = new ChooseClassesInput(HUMAN_CLASSES, NUM_CLASSES_TO_PICK, MAX_PER_SAME_CLASS, turn);
        Map<CharacterClassType, Integer> chosenClasses = human.getChosenClassesInput(chooseClassesInput);
        applyChosenClassesToHumans(chosenClasses, chooseClassesInput);

        // Generate character diff
        for (CharacterState characterState : characterStates.values()) {
            String id = characterState.getId();
            Map<String, JsonNode> diff = characterState.diff(null);
            characterStateDiffs.put(id, diff);
        }

        // Load terrain
        Map<String, Map<String, JsonNode>> terrainStateDiffs = new HashMap<>();

        int i = 0;
        for (int y = 0; y < map.size(); y++) {
            List<Character> row = map.get(y);
            for (int x = 0; x < row.size(); x++) {
                String id = Integer.toString(i);

                Character character = row.get(x);

                if (character == 'e') {
                    continue;
                }

                TerrainType terrainType = MAP_CHAR_TO_TERRAIN_TYPE.get(character);

                if (terrainType == null) {
                    System.err.printf("Error processing map: %s is not a valid terrain type\n", character);
                    continue;
                }

                TerrainData terrainData = TERRAIN_DATAS.get(terrainType);
                Position position = new Position(x, y);

                TerrainState terrainState = new TerrainState(id, terrainData, position);
                terrainStates.put(id, terrainState);

                Map<String, JsonNode> diff = terrainState.diff(null);
                terrainStateDiffs.put(id, diff);

                i += 1;
            }
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

        // Get player ability input
        Map<String, List<AbilityAction>> possibleAbilityActions = getPossibleAbilityActions(player.isZombie());
        List<AbilityAction> abilityActions = player.getAbilityInput(
                new AbilityInput(possibleAbilityActions, turn, characterStates, terrainStates)
        );

        // Apply ability actions
        applyAbilityActions(abilityActions, possibleAbilityActions);

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
            log.storeResults(getScores(), getStats(), getErrors());
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

    public LogStats getStats() {
        int zombiesCount = getZombiesCount();
        int humansCount = getHumansCount();
        double humansAverageTime = humanPlayer.getAverageTime();
        double zombiesAverageTime = zombiePlayer.getAverageTime();
        return new LogStats(turn, humansCount, zombiesCount, humansAverageTime, zombiesAverageTime);
    }

    public LogErrors getErrors() {
        return new LogErrors(humanPlayer.getErrorLogger().getLogs(), zombiePlayer.getErrorLogger().getLogs());
    }

    private void handleInvalidInputError(boolean isZombie, GamePhase phase, String got, String expected) {
        PlayerErrorLogger errorLogger = isZombie ? zombiePlayer.getErrorLogger() : humanPlayer.getErrorLogger();
        errorLogger.log(String.format(
                "%s player provided an invalid input on turn %d during %s phase: %s\nExpected (one of): %s",
                isZombie ? "zombie" : "human", turn, phase,got, expected
        ));
    }

    private void applyClearActions(Map<String, CharacterState> characterStates) {
        characterStates.values().forEach(CharacterState::clearActions);
    }

    private void applyChosenClassesToHumans(Map<CharacterClassType, Integer> chosen, ChooseClassesInput chooseClassesInput) {
        List<CharacterClassType> possibleChoices = chooseClassesInput.choices();
        int numToPick = chooseClassesInput.numToPick();
        int maxPerSameClass = chooseClassesInput.maxPerSameClass();

        List<CharacterClassType> spreadChosen = SpreadMap.spread(chosen);

        if (spreadChosen.size() > numToPick) {
            handleInvalidInputError(false, GamePhase.CHOOSE_CLASSES,
                    String.format("%d chosen classes", spreadChosen.size()),
                    String.format("%d max chosen classes", numToPick));
        }

        int picked = 0;
        Map<CharacterClassType, Integer> classCounts = new HashMap<>();
        while (picked < numToPick && !spreadChosen.isEmpty()) {
            CharacterClassType selected = spreadChosen.remove(0);

            if (!possibleChoices.contains(selected)) {
                handleInvalidInputError(false, GamePhase.CHOOSE_CLASSES,
                        selected.toString(), possibleChoices.toString());
                continue;
            }

            if (!classCounts.containsKey(selected)) {
                classCounts.put(selected, 0);
            }

            int currentCount = classCounts.get(selected);
            if (currentCount < maxPerSameClass) {
                classCounts.put(selected, currentCount + 1);
                picked += 1;
            } else {
                handleInvalidInputError(false, GamePhase.CHOOSE_CLASSES,
                        String.format("%d of %s class", picked, selected),
                        String.format("%d max of %s class", maxPerSameClass, selected));
            }
        }

        List<CharacterState> humans = characterStates.values().stream().filter(character -> !character.isZombie()).toList();

        while (picked < humans.size()) {
            CharacterClassType selected = CharacterClassType.NORMAL;

            if (!classCounts.containsKey(selected)) {
                classCounts.put(selected, 0);
            }

            int currentCount = classCounts.get(selected);
            classCounts.put(selected, currentCount + 1);
            picked += 1;
        }

        List<CharacterClassType> classes = SpreadMap.spread(classCounts);

        for (int i = 0; i < humans.size(); i++) {
            CharacterState human = humans.get(i);
            CharacterClassType characterClassType = classes.get(i);
            human.applyClass(characterClassType);
        }
    }

    private void applyMoveActions(List<MoveAction> moveActions, Map<String, List<MoveAction>> possibleMoveActions) {
        for (String id : possibleMoveActions.keySet()) {
            CharacterState executing = characterStates.get(id);
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
                handleInvalidInputError(executing.isZombie(), GamePhase.MOVE,
                        moveAction.toString(), possibleMoves.toString());
                continue;
            }


            // Apply move action
            executing.setPosition(moveAction.getDestination());
        }
    }

    private void applyAttackActions(List<AttackAction> attackActions, Map<String, List<AttackAction>> possibleAttackActions) {
        for (String id : possibleAttackActions.keySet()) {
            CharacterState executing = characterStates.get(id);
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
                handleInvalidInputError(executing.isZombie(), GamePhase.ATTACK,
                        attackAction.toString(), possibleAttacks.toString());
                continue;
            }

            // Apply attack action
            AttackActionType attackType = attackAction.getType();
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

                // One shot ability
                if (executing.getAbilities().contains(CharacterClassAbility.ONESHOT_TERRAIN) && attacking.isDestroyable()) {
                    while (!attacking.isDestroyed()) {
                        attacking.attack();
                    }
                }
            }
            executing.setAttackAction(attackAction);
            executing.resetAttackCooldownLeft();
        }
    }

    private void applyAbilityActions(List<AbilityAction> abilityActions, Map<String, List<AbilityAction>> possibleAbilityActions) {
        for (String id : possibleAbilityActions.keySet()) {
            CharacterState executing = characterStates.get(id);
            List<AbilityAction> possibleActions = possibleAbilityActions.get(id);
            List<AbilityAction> attemptedActions = abilityActions.stream()
                    .filter(action -> action.getExecutingCharacterId().equals(id))
                    .toList();

            if (attemptedActions.isEmpty()) {
                continue;
            }

            // Only register the first action
            AbilityAction abilityAction = attemptedActions.get(0);

            // Check if possible
            boolean possible = false;
            for (AbilityAction possibleMove : possibleActions) {
                if (abilityAction.equals(possibleMove)) {
                    possible = true;
                    break;
                }
            }

            if (!possible) {
                handleInvalidInputError(executing.isZombie(), GamePhase.ABILITY,
                        abilityAction.toString(), possibleActions.toString());
                continue;
            }

            // Apply action
            AbilityActionType abilityType = abilityAction.getType();

            if (abilityType == AbilityActionType.HEAL) {
                // Handle healing ability
                CharacterState healing = characterStates.get(abilityAction.getCharacterIdTarget());

                healing.setHealth(healing.getHealth() + 1);
            } else if (abilityType == AbilityActionType.BUILD_BARRICADE) {
                // Handle build ability
                Position newPosition = abilityAction.getPositionalTarget();
                String newId = Integer.toString(terrainStates.size());
                TerrainData newData = TERRAIN_DATAS.get(TerrainType.BARRICADE);

                terrainStates.put(newId, new TerrainState(newId, newData, newPosition));
            }
            executing.setAbilityAction(abilityAction);
            executing.resetAbilityCooldownLeft();
        }
    }

    private Map<String, Position> getTilesInRange(Position start, int range, boolean isAttack, boolean ignoreBarricades) {
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

                if (!isAttack && ignoreBarricades && terrainState.getType() == TerrainType.BARRICADE) {
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
            Map<String, Position> fromThere = getTilesInRange(newPosition, range - 1, isAttack, ignoreBarricades);

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
            int range = characterState.canMove() ? characterState.getMoveSpeed() : -1;
            boolean ignoreBarricades = characterState.getAbilities().contains(CharacterClassAbility.MOVE_OVER_BARRICADES);
            Map<String, Position> moves = getTilesInRange(characterState.getPosition(), range, false, ignoreBarricades);

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
            int range = characterState.canAttack() ? characterState.getAttackRange() : -1;
            Map<String, Position> attackable = getTilesInRange(characterState.getPosition(), range, true, false);
            List<AttackAction> attackActions = new ArrayList<>();

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

            // Add all attack actions for character
            possibleAttackActions.put(characterState.getId(), attackActions);
        }

        return possibleAttackActions;
    }

    private Map<String, List<AbilityAction>> getPossibleAbilityActions(boolean isZombie) {
        // Get controllable character states
        Map<String, CharacterState> controllableCharacterStates = new HashMap<>();

        for (CharacterState characterState : characterStates.values()) {
            if (characterState.isZombie() == isZombie) {
                controllableCharacterStates.put(characterState.getId(), characterState);
            }
        }

        // Get possible moves for each character
        Map<String, List<AbilityAction>> possibleActions = new HashMap<>();

        for (CharacterState executing : controllableCharacterStates.values()) {
            String executingId = executing.getId();
            int range = executing.canAbility() ? executing.getAttackRange() : -1;
            Map<String, Position> locations = getTilesInRange(executing.getPosition(), range, false, false);
            List<AbilityAction> actions = new ArrayList<>();

            for (Position location : locations.values()) {
                if (executing.getAbilities().contains(CharacterClassAbility.HEAL)) {
                    Optional<CharacterState> targetMaybe = characterStates.values().stream().filter(pos -> pos.getPosition().equals(location)).findFirst();

                    if (targetMaybe.isEmpty()) {
                        continue;
                    }

                    CharacterState target = targetMaybe.get();
                    String targetId = target.getId();

                    if (Objects.equals(targetId, executingId)) {
                        continue;
                    }

                    actions.add(new AbilityAction(executingId, AbilityActionType.HEAL, null, targetId));
                } else if (executing.getAbilities().contains(CharacterClassAbility.BUILD_BARRICADE)) {
                    long characterOccupying = characterStates.values().stream().filter(characterState -> characterState.getPosition().equals(location)).count();

                    if (characterOccupying > 0) {
                        continue;
                    }

                    long terrainOccupying = terrainStates.values().stream().filter(terrainState -> terrainState.getPosition().equals(location)).count();

                    if (terrainOccupying > 0) {
                        continue;
                    }

                    actions.add(new AbilityAction(executingId, AbilityActionType.BUILD_BARRICADE, location, null));
                }
            }

            possibleActions.put(executingId, actions);
        }

        return possibleActions;
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
