package mech.mania.engine;

import mech.mania.engine.GameState;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.player.ComputerPlayer;
import mech.mania.engine.player.Player;
import mech.mania.engine.util.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mech.mania.engine.Config.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {
    @Test
    public void BaseTest() throws IOException {
        Player human = new ComputerPlayer(false);
        Player zombie = new ComputerPlayer(true);
        GameState gameState = new GameState(human, zombie, List.of());

        int zombiesCount = 0;
        int humansCounts = 0;
        int totalCount = 0;

        for (CharacterState characterState : gameState.getCharacterStates().values()) {
            if (characterState.isZombie()) {
                zombiesCount += 1;
            } else {
                humansCounts += 1;
            }

            totalCount += 1;
        }

        assertEquals(STARTING_ZOMBIES, zombiesCount);
        assertEquals(TOTAL_CHARACTERS - STARTING_ZOMBIES, humansCounts);
        assertEquals(TOTAL_CHARACTERS, totalCount);
    }

    private <K, V> void assertMapsEqual(Map<K, V> expected, Map<K, V> got) {
        assertEquals(expected.size(), got.size(), String.format("Maps have different sizes: expected: %s, got: %s", expected, got));

        for (K key : expected.keySet()) {
            if (!got.keySet().contains(key)) {
                assertEquals(key, null, String.format("Got has missing key: expected: %s, got: %s", expected, got));
                return;
            }
            if (!got.get(key).equals(expected.get(key))) {
                assertEquals(got.get(key), expected.get(key), String.format("Got has incorrect value: expected: %s, got: %s", expected, got));
                return;
            }
        }

        for (K key : got.keySet()) {
            if (!expected.keySet().contains(key)) {
                assertEquals(null, key, String.format("Got has extra key: expected: %s, got: %s", expected, got));
                return;
            }
        }
    }

    @Test
    public void getTilesInRangeBasicTest() {
        Player human = new ComputerPlayer(false);
        Player zombie = new ComputerPlayer(true);
        GameState empty = new GameState(human, zombie, List.of());

        Position center = new Position(BOARD_SIZE / 2, BOARD_SIZE / 2);
        Map<String, Position> range0Expected = Map.of(center.toString(), center);
        Map<String, Position> range0 = empty.getTilesInRange(center, 0, false, false, false);

        assertMapsEqual(range0Expected, range0);

        Map<String, Position> range1Expected = Stream.of(
                center,
                new Position(center.getX() + 1, center.getY()),
                new Position(center.getX() - 1, center.getY()),
                new Position(center.getX(), center.getY() + 1),
                new Position(center.getX(), center.getY() - 1)
        ).collect(Collectors.toMap(Position::toString, pos -> pos));
        Map<String, Position> range1 = empty.getTilesInRange(center, 1, false, false, false);

        assertMapsEqual(range1Expected, range1);

        Map<String, Position> range2Expected = Stream.of(
                center,
                new Position(center.getX() + 1, center.getY()),
                new Position(center.getX() - 1, center.getY()),
                new Position(center.getX(), center.getY() + 1),
                new Position(center.getX(), center.getY() - 1),
                new Position(center.getX() + 1, center.getY() + 1),
                new Position(center.getX() - 1, center.getY() - 1),
                new Position(center.getX() - 1, center.getY() + 1),
                new Position(center.getX() + 1, center.getY() - 1),
                new Position(center.getX() + 2, center.getY()),
                new Position(center.getX() - 2, center.getY()),
                new Position(center.getX(), center.getY() + 2),
                new Position(center.getX(), center.getY() - 2)
        ).collect(Collectors.toMap(Position::toString, pos -> pos));
        Map<String, Position> range2 = empty.getTilesInRange(center, 2, false, false, false);

        assertMapsEqual(range2Expected, range2);
    }

    @Test
    public void getTilesInRangeDiagonalTest() {
        Player human = new ComputerPlayer(false);
        Player zombie = new ComputerPlayer(true);
        GameState empty = new GameState(human, zombie, List.of());

        Position center = new Position(BOARD_SIZE / 2, BOARD_SIZE / 2);
        Map<String, Position> range0Expected = Map.of(center.toString(), center);
        Map<String, Position> range0 = empty.getTilesInRange(center, 0, true, false, false);

        assertMapsEqual(range0Expected, range0);

        Map<String, Position> range1Expected = Stream.of(
                center,
                new Position(center.getX() + 1, center.getY()),
                new Position(center.getX() - 1, center.getY()),
                new Position(center.getX(), center.getY() + 1),
                new Position(center.getX(), center.getY() - 1),
                new Position(center.getX() + 1, center.getY() + 1),
                new Position(center.getX() - 1, center.getY() - 1),
                new Position(center.getX() - 1, center.getY() + 1),
                new Position(center.getX() + 1, center.getY() - 1)
        ).collect(Collectors.toMap(Position::toString, pos -> pos));
        Map<String, Position> range1 = empty.getTilesInRange(center, 1, true, false, false);

        assertMapsEqual(range1Expected, range1);
    }

    @Test
    public void getTilesInRangeTerrainTest() {
        Player human = new ComputerPlayer(false);
        Player zombie = new ComputerPlayer(true);

        List<List<Character>> map = List.of(
                "rrrrr".chars().mapToObj(ch -> (char) ch).toList(),
                "rrrrr".chars().mapToObj(ch -> (char) ch).toList(),
                "rrerr".chars().mapToObj(ch -> (char) ch).toList(),
                "rrrrr".chars().mapToObj(ch -> (char) ch).toList(),
                "rrrrr".chars().mapToObj(ch -> (char) ch).toList()
        );

        GameState empty = new GameState(human, zombie, map);

        Position middle = new Position(2, 2);
        Map<String, Position> rangeExpected = Map.of(middle.toString(), middle);
        Map<String, Position> range = empty.getTilesInRange(middle, 5, false, false, false);

        assertMapsEqual(rangeExpected, range);

        Map<String, Position> rangeAttackExpected = Stream.of(
                middle,
                new Position(middle.getX() + 1, middle.getY()),
                new Position(middle.getX() - 1, middle.getY()),
                new Position(middle.getX(), middle.getY() + 1),
                new Position(middle.getX(), middle.getY() - 1)
        ).collect(Collectors.toMap(Position::toString, pos -> pos));
        Map<String, Position> rangeAttack = empty.getTilesInRange(middle, 5, false, true, false);

        assertMapsEqual(rangeAttackExpected, rangeAttack);
    }
}
