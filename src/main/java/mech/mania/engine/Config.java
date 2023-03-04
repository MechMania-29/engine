package mech.mania.engine;

import mech.mania.engine.util.Position;

import java.io.Serializable;
import java.util.*;

public class Config implements Serializable {
    public static final int BOARD_SIZE = 100;
    public static final int TURNS = 100 * 2; // This means 100 turns for each player
    public static final int TOTAL_CHARACTERS = 25;
    public static final int STARTING_ZOMBIES = 5;

    public static final int HUMAN_MOVE_SPEED = 3;
    public static final int ZOMBIE_MOVE_SPEED = 5;

    public static final List<Position> DIRECTIONS = Arrays.asList(
            new Position(0, 1), // DOWN
            new Position(0, -1), // UP
            new Position(1, 0), // RIGHT
            new Position(-1, 0) // LEFT
    );

    public static final List<String> TERRAIN_IMAGE_IDS = Arrays.asList(
            "rock",
            "tree",
            "building"
    );
}
