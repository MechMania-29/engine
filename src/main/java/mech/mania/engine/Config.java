package mech.mania.engine;

import mech.mania.engine.util.Position;

import java.io.Serializable;
import java.util.*;

public class Config implements Serializable {
    // The size of the board, always square
    public static final int BOARD_SIZE = 100;
    // The number of turns in a game, each player gets their own turn
    public static final int TURNS = 100 * 2; // This means 100 turns for each player
    // The total number of characters in the game
    public static final int TOTAL_CHARACTERS = 25;
    // The number of those that start as zombies
    public static final int STARTING_ZOMBIES = 5;

    // Default move speeds for humans and zombies
    public static final int HUMAN_MOVE_SPEED = 3;
    public static final int ZOMBIE_MOVE_SPEED = 5;

    // Directions that characters can move
    public static final List<Position> DIRECTIONS = Arrays.asList(
            new Position(0, 1), // DOWN
            new Position(0, -1), // UP
            new Position(1, 0), // RIGHT
            new Position(-1, 0) // LEFT
    );

    // These are the various ids for terrain. Likely will need to be updated.
    public static final List<String> TERRAIN_IMAGE_IDS = Arrays.asList(
            "rock",
            "tree",
            "building"
    );
}
