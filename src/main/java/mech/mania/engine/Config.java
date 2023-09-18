package mech.mania.engine;

import mech.mania.engine.character.CharacterClassData;
import mech.mania.engine.character.CharacterClassAbility;
import mech.mania.engine.character.CharacterClassType;
import mech.mania.engine.terrain.TerrainData;
import mech.mania.engine.terrain.TerrainType;
import mech.mania.engine.util.Position;

import java.io.Serializable;
import java.util.*;

public class Config implements Serializable {
    // This controls whether the diff serialization is used or not. Turning this to false
    // significantly increases the size of the game log, but may prevent weird sync issues
    // depending on various cases. DO NOT CHANGE without talking to Timothy.
    public static final boolean DIFF_MODE_ENABLED = true;
    // The size of the board, always square
    public static final int BOARD_SIZE = 100;
    // The number of turns in a game, each player gets their own turn
    public static final int TURNS = 100 * 2; // This means 100 turns for each player
    // The total number of characters in the game
    public static final int TOTAL_CHARACTERS = 25;
    // The number of those that start as zombies
    public static final int STARTING_ZOMBIES = 5;

    // Classes
    public static final int MAX_PER_SAME_CLASS = 10;
    public static final int NUM_CLASSES_TO_PICK = 16;

    public static final Map<CharacterClassType, CharacterClassData> CLASSES = Map.of(
            CharacterClassType.NORMAL, new CharacterClassData(
                    1, 3, 4, 3,
                    List.of()
            ),
            CharacterClassType.ZOMBIE, new CharacterClassData(
                    -1, 5, 1, 0, // Zombie health of -1 symbolically means infinite health
                    List.of()
            ),
            CharacterClassType.MARKSMAN, new CharacterClassData(
                    1, 3, 6, 3,
                    List.of()
            ),
            CharacterClassType.TRACEUR, new CharacterClassData(
                    1, 4, 2, 2,
                    List.of(CharacterClassAbility.MOVE_OVER_BARRICADES)
            ),
            CharacterClassType.MEDIC, new CharacterClassData(
                    2, 3, 3, 3,
                    List.of(CharacterClassAbility.HEAL)
            ),
            CharacterClassType.BUILDER, new CharacterClassData(
                    1, 3, 4, 3,
                    List.of(CharacterClassAbility.BUILD_BARRICADE)
            ),
            CharacterClassType.DEMOLITIONIST, new CharacterClassData(
                    1, 3, 2, 3,
                    List.of(CharacterClassAbility.ONESHOT_TERRAIN)
            )
    );

    public static final List<CharacterClassType> HUMAN_CLASSES = Arrays.asList(
            CharacterClassType.NORMAL, CharacterClassType.MARKSMAN, CharacterClassType.TRACEUR,
            CharacterClassType.MEDIC, CharacterClassType.BUILDER, CharacterClassType.DEMOLITIONIST
    );

    // Other cooldowns
    public static final int ABILITY_COOLDOWN = 6;

    // The duration that a zombie is stunned for
    public static final int STUNNED_DURATION = 1;

    // Directions that characters can move
    public static final List<Position> DIRECTIONS = Arrays.asList(
            new Position(0, 1), // DOWN
            new Position(0, -1), // UP
            new Position(1, 0), // RIGHT
            new Position(-1, 0) // LEFT
    );

    // These are the various ids for terrain. Likely will need to be updated.
    public static final Map<TerrainType, TerrainData> TERRAIN_DATAS = Map.of(
            TerrainType.WALL, new TerrainData(TerrainType.WALL, "wall", false, 3),
            TerrainType.BARRICADE, new TerrainData(TerrainType.BARRICADE, "barricade", true, 1),
            TerrainType.TREE, new TerrainData(TerrainType.TREE, "tree", false, 2),
            TerrainType.RIVER, new TerrainData(TerrainType.RIVER, "river", true, -1) // -1 makes invincible
    );

    public static final Map<Character, TerrainType> MAP_CHAR_TO_TERRAIN_TYPE = Map.of(
            'w', TerrainType.WALL,
            't', TerrainType.TREE,
            'r', TerrainType.RIVER
    );

    // Networking
    public static final int TIMEOUT_MILIS_INIT = 15 * 1000; // The timeout for initial connection
    public static final int TIMEOUT_MILIS_TURN = 2500; // The timout for each turn response
}
