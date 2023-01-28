package mech.mania.engine;

import mech.mania.engine.character.Position;

import java.io.Serializable;
import java.util.*;

public class Config implements Serializable {
    public static final int BOARD_SIZE = 100;
    public static final int TURNS = 100 * 2; // This means 100 turns for each player
    public static final int TOTAL_CHARACTERS = 25;
    public static final int STARTING_ZOMBIES = 5;

    public static final List<Position> DIRECTIONS = Arrays.asList(
            new Position(1, 0), // DOWN
            new Position(-1, 0), // UP
            new Position(0, 1), // RIGHT
            new Position(0, -1), // LEFT
            new Position(1, 1), // DOWN_RIGHT
            new Position(1, -1), // UP_RIGHT
            new Position(-1, 1), // DOWN_LEFT
            new Position(-1, -1) // UP_LEFT
    );
}
